package code.distribution.raft.log;

import code.distribution.raft.*;
import code.distribution.raft.enums.RoleType;
import code.distribution.raft.model.AppendEntriesReq;
import code.distribution.raft.model.AppendEntriesRet;
import code.distribution.raft.model.Command;
import code.distribution.raft.model.LogEntry;
import code.distribution.raft.rpc.RpcService;
import code.util.ConcurrentHashSet;
import code.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈日志复制 发送器〉<p>
 * <p>
 * 如果在不同的日志中的两个条目拥有相同的索引和任期号，那么他们存储了相同的指令。
 * 如果在不同的日志中的两个条目拥有相同的索引和任期号，那么他们之前的所有日志条目也全部相同。
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class AppendEntriesSender implements ISender<AppendEntriesReq, AppendEntriesRet>, IService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppendEntriesSender.class);

    private final RaftNode node;

    private final RaftNodeServer nodeServer;

    /**
     * 日志复制失败 待重发列表
     * nodeId => 复制截止Index
     */
    private final Map<String, Integer> toRetryAppendNodeMap = new ConcurrentHashMap<>();

    private RejectedExecutionHandler rejectedExecutionHandler = (r, executor) -> {
        executor.getQueue().poll();
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    };

    /**
     * 日志复制失败重发定时器
     */
    private final ScheduledExecutorService retryAppendEntriesTimer = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("RetryAppendEntriesTimer-"), rejectedExecutionHandler);

    /**
     * 心跳定时器
     */
    private final ScheduledExecutorService heartbeatTimer = new ScheduledThreadPoolExecutor(1,
            new NamedThreadFactory("HeartbeatTimer-"), rejectedExecutionHandler);

    private AtomicBoolean running = new AtomicBoolean(false);

    public AppendEntriesSender(RaftNode node, RaftNodeServer nodeServer) {
        this.node = node;
        this.nodeServer = nodeServer;
    }

    @Override
    public void start() {
        running.set(true);

        retryAppendEntriesTimer.scheduleAtFixedRate(() -> {
            Iterator<Map.Entry<String, Integer>> iterator = toRetryAppendNodeMap.entrySet().iterator();
            int commitIndex = node.getCommitIndex().get();
            while (running.get() && node.getRole() == RoleType.LEADER && iterator.hasNext()){
                Map.Entry<String, Integer> entry = iterator.next();
                //复制成功，把改node从重试列表删除
                if(replication(entry.getKey(), entry.getValue(), commitIndex)){
                    iterator.remove();
                }
            }
            //如果不再是leader清空
            if(node.getRole() != RoleType.LEADER){
                toRetryAppendNodeMap.clear();
            }
        }, 0, RaftConst.RETRY_APPEND_MS, TimeUnit.MILLISECONDS);

        heartbeatTimer.scheduleAtFixedRate(() -> {
            Set<String> nodeSet = RaftNetwork.clusterNodeIds(node.getNodeId());
            nodeSet.forEach(nodeId -> {
                if (running.get() && node.getRole() == RoleType.LEADER) {
                    heartbeat(nodeId);
                }
            });
        }, 0, RaftConst.HEARTBEAT_MS, TimeUnit.MILLISECONDS);
    }

    public synchronized boolean appendEntries(Command command) {
        int currentTerm = node.getCurrentTerm().get();
        int commitIndex = node.getCommitIndex().get();

        //预提交
        LogEntry logEntry = new LogEntry(currentTerm, command);
        LOGGER.info("预提交日志，{}", logEntry);
        node.getLogModule().append(logEntry);

        int newLogIndex = node.getLogModule().lastLogIndex();

        AtomicInteger successNum = new AtomicInteger();
        //需要重试的节点列表
        Map<String, Integer> tempRetryNodeMap = new HashMap<>();
        node.getNextIndex().keySet().parallelStream().forEach((nodeId) -> {
            if (replication(nodeId, newLogIndex, commitIndex)) {
                successNum.addAndGet(1);
            } else {
                tempRetryNodeMap.put(nodeId, newLogIndex);
            }
        });

        //多数派复制成功
        if (successNum.get() >= RaftNetwork.nodeNum() / 2) {
            LOGGER.info("多数派复制成功，success:{}， 提交日志，commitIndex:{}", successNum, newLogIndex);
            //日志提交, commitIndex在下次AppendEntries给其他节点（也可以放在心跳）
            node.setCommitIndex(newLogIndex);

            //失败的节点 添加重试列表
            toRetryAppendNodeMap.putAll(tempRetryNodeMap);

            //应用状态机，TODO 异步处理
            node.apply(logEntry, newLogIndex);
            return true;
        } else {
            LOGGER.info("多数派复制失败，success:{}，回滚.", successNum);
            //回滚提交
            node.getLogModule().removeFrom(newLogIndex);

            //清空重试列表
            tempRetryNodeMap.clear();
            return false;
        }
    }

    /**
     * 复制日志
     *
     * @param nodeId        节点
     * @param newLogIndex   最新的日志位置
     * @param commitIndex
     * @return
     */
    private boolean replication(String nodeId, int newLogIndex, int commitIndex) {
        // 从待发送的位置
        int nextIndex = node.getNextIndex().get(nodeId);
        if(newLogIndex < nextIndex){
            //从nextIndex至newLogIndex的日志已被复制，直接返回成功
            return true;
        }
        LOGGER.info(">>>开始复制日志到节点 {}, nextIndex:{} -> newLogIndex:"+newLogIndex, nodeId, nextIndex);

        try {
            // 从待发送的位置开始复制到当前位置
            List<LogEntry> copyLogs = node.getLogModule().subLogs(nextIndex, newLogIndex);

            AppendEntriesReq req = buildReq(nextIndex, copyLogs, commitIndex);
            AppendEntriesRet ret = send(nodeId, req);
            if (ret == null) {
                //Append rpc fail
                return false;
            }
            if (ret.isSuccess()) {
                LOGGER.info("<<<复制日志到节点 {} 成功", nodeId);
                node.getNextIndex().put(nodeId, newLogIndex + 1);
                node.getMatchIndex().put(nodeId, newLogIndex);
                return true;
            } else {
                int myTerm = node.currentTerm();
                LOGGER.info("<<<复制日志到节点 {} 失败，myTerm: {}, responseTerm: " + ret.getTerm(), nodeId, myTerm);
                if (ret.getTerm() > myTerm) {
                    //退位
                    changeToFollower(myTerm, ret.getTerm());
                } else {
                    //复制失败，nextIndex-1
                    node.getNextIndex().put(nodeId, nextIndex - 1);
                }
                return false;
            }
        } catch (Exception e) {
            LOGGER.error("复制日志到节点 " + nodeId + " 失败", e);
            return false;
        }
    }

    private AppendEntriesReq buildReq(int nextIndex, List<LogEntry> copyLogs, int commitIndex) {
        //待复制的上一个日志
        int prevIndex = nextIndex - 1;
        int prevTerm = RaftConst.EMPTY_TERM;
        if (prevIndex >= 0) {
            prevTerm = node.getLogModule().indexOf(prevIndex).getTerm();
        }
        LogEntry[] entries = copyLogs.toArray(new LogEntry[copyLogs.size()]);
        return AppendEntriesReq.build(node.currentTerm(), node.getNodeId(), prevIndex, prevTerm, entries, commitIndex);
    }

    /**
     * 发送心跳
     *
     * @param nodeId
     */
    private void heartbeat(String nodeId) {
        AppendEntriesReq appendEntriesReq = AppendEntriesReq.buildHeartbeat(node.currentTerm(), node.getNodeId(), node.getCommitIndex().get());
        LOGGER.debug("Send heartbeat to {}", nodeId);
        AppendEntriesRet heartbeatRet = send(nodeId, appendEntriesReq);
        if (heartbeatRet != null && !heartbeatRet.isSuccess()) {
            int nowTerm = node.currentTerm();
            if (heartbeatRet.getTerm() > nowTerm) {
                changeToFollower(nowTerm, heartbeatRet.getTerm());
            }
        }
    }

    private void changeToFollower(int nowTerm, int newTerm) {
        if (node.getCurrentTerm().compareAndSet(nowTerm, newTerm)) {
            nodeServer.changeToFollower(newTerm);
        }
    }

    @Override
    public void close() {
        running.set(false);
        retryAppendEntriesTimer.shutdownNow();
        heartbeatTimer.shutdownNow();
    }

    @Override
    public AppendEntriesRet send(String nodeId, AppendEntriesReq req) {
        return RpcService.getInstance().appendEntries(nodeId, req);
    }
}
