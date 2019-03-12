package code.distribution.raft.log;

import code.distribution.raft.*;
import code.distribution.raft.model.AppendEntriesReq;
import code.distribution.raft.model.AppendEntriesRet;
import code.distribution.raft.model.EntryIndex;
import code.distribution.raft.model.LogEntry;
import code.distribution.raft.rpc.RpcService;
import code.util.NamedThreadFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 〈日志复制 发送器〉<p>
 *
 * 如果在不同的日志中的两个条目拥有相同的索引和任期号，那么他们存储了相同的指令。
 * 如果在不同的日志中的两个条目拥有相同的索引和任期号，那么他们之前的所有日志条目也全部相同。
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class AppendEntriesSender implements ISender<AppendEntriesReq, AppendEntriesRet>, IService {

    private final RaftNode node;

    private final RaftNodeServer nodeServer;

    private final ScheduledExecutorService appendEntriesTimer = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("AppendEntriesTimer-"), new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            executor.getQueue().poll();
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    });

    private AtomicBoolean running = new AtomicBoolean(true);

    public AppendEntriesSender(RaftNode node, RaftNodeServer nodeServer) {
        this.node = node;
        this.nodeServer = nodeServer;
    }

    @Override
    public void start() {
        appendEntriesTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!running.compareAndSet(true, false)) {
                    return;
                }
                try {
                    broadcastAppendEntries();
                } finally {
                    running.compareAndSet(false, true);
                }
            }
        }, 0, RaftConst.HEARTBEAT_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * 广播发送
     */
    public void broadcastAppendEntries() {
        List<LogEntry> copyLogs = new ArrayList<>(node.getLogs());
        int commitIndex = node.getCommitIndex().get();
        node.getNextIndex().parallelStream().forEach(nextIndex -> {
            if (nextIndex.getIndex() < copyLogs.size()) {
                appendEntries(nextIndex, copyLogs, commitIndex);
            } else {
                heartbeat(nextIndex.getNodeId());
            }
        });
    }

    /**
     * 复制日志
     * @param nextIndex
     * @param copyLogs
     * @param commitIndex
     */
    private void appendEntries(EntryIndex nextIndex, List<LogEntry> copyLogs, int commitIndex){
        AppendEntriesReq req = buildReq(nextIndex, copyLogs, commitIndex);
        AppendEntriesRet ret = send(nextIndex.getNodeId(), buildReq(nextIndex, copyLogs, commitIndex));
        if(ret.isSuccess()){
            nextIndex.setIndex(nextIndex.getIndex() + req.getEntries().length);
        }else{
            int nowTerm = node.currentTerm();
            if(ret.getTerm() > nowTerm){
                changeToFollower(nowTerm, req.getTerm());
            }
        }
    }

    private AppendEntriesReq buildReq(EntryIndex nextIndex, List<LogEntry> copyLogs, int commitIndex){
        int prevIndex = nextIndex.getIndex() - 1;
        int prevTerm = RaftConst.EMPTY_TERM;
        if(prevIndex >= 0){
            prevTerm = copyLogs.get(prevIndex).getTerm();
        }
        LogEntry[] entries = (LogEntry[])copyLogs.subList(nextIndex.getIndex(), copyLogs.size()).toArray();
        return AppendEntriesReq.build(node.currentTerm(), node.getNodeId(), prevIndex, prevTerm, entries, commitIndex);
    }

    /**
     * 发送心跳
     * @param nodeId
     */
    private void heartbeat(String nodeId) {
        AppendEntriesReq appendEntriesReq = AppendEntriesReq.buildHeartbeat(node.currentTerm(), node.getNodeId());
        AppendEntriesRet heartbeatRet = send(nodeId, appendEntriesReq);
        if(!heartbeatRet.isSuccess()){
            int nowTerm = node.currentTerm();
            if(heartbeatRet.getTerm() > nowTerm){
                changeToFollower(nowTerm, heartbeatRet.getTerm());
            }
        }
    }

    private void changeToFollower(int nowTerm, int newTerm){
        if(node.getCurrentTerm().compareAndSet(nowTerm, newTerm)){
            nodeServer.changeToFollower(newTerm);
        }
    }

    @Override
    public void close() {
        running.compareAndSet(true, false);
        appendEntriesTimer.shutdownNow();
    }

    @Override
    public AppendEntriesRet send(String nodeId, AppendEntriesReq req) {
        return RpcService.getInstance().appendEntries(nodeId, req);
    }
}
