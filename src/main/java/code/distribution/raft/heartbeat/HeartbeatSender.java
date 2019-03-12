package code.distribution.raft.heartbeat;

import code.distribution.raft.*;
import code.distribution.raft.model.AppendEntriesReq;
import code.distribution.raft.model.AppendEntriesRet;
import code.distribution.raft.rpc.RpcService;
import code.util.NamedThreadFactory;

import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 〈心跳发送器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class HeartbeatSender implements ISender<AppendEntriesReq, AppendEntriesRet>, IService {

    private final RaftNode node;

    private AtomicBoolean running = new AtomicBoolean(true);

    private ScheduledExecutorService heartbeatTimer = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("HeartbeatTimer-"), new RejectedExecutionHandler() {
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

    public HeartbeatSender(RaftNode node) {
        this.node = node;
    }

    @Override
    public void start() {
        heartbeatTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(!running.compareAndSet(true, false)){
                    return;
                }
                try{
                    heartbeat();
                }finally {
                    running.compareAndSet(false, true);
                }
            }
        }, 0, RaftConst.HEARTBEAT_MS, TimeUnit.MILLISECONDS);
    }

    private void heartbeat() {
        String myNodeId = node.getNodeId();
        Set<String> nodeIdSet = RaftNetwork.clusterNodeIds(myNodeId);
        AppendEntriesReq appendEntriesReq = AppendEntriesReq.buildHeartbeat(node.currentTerm(), myNodeId);
        nodeIdSet.parallelStream().forEach(nodeId -> {
            if(!running.get()){
                AppendEntriesRet ret = send(nodeId, appendEntriesReq);
                //TODO 处理心跳返回结果
            }
        });
    }

    @Override
    public void close() {
        running.compareAndSet(true, false);
        heartbeatTimer.shutdownNow();
    }

    @Override
    public AppendEntriesRet send(String nodeId, AppendEntriesReq appendEntriesReq) {
        return RpcService.getInstance().appendEntries(nodeId, appendEntriesReq);
    }

}
