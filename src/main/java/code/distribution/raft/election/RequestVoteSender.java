package code.distribution.raft.election;

import code.distribution.raft.*;
import code.distribution.raft.model.RequestVoteReq;
import code.distribution.raft.model.RequestVoteRet;
import code.distribution.raft.rpc.RpcService;
import code.util.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * 〈心跳发送器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class RequestVoteSender implements ISender<RequestVoteReq, RequestVoteRet>, IService {

    private final RaftNode node;

    private final ExecutorService broadcastExecutor;

    public RequestVoteSender(RaftNode node) {
        this.node = node;
        this.broadcastExecutor = ThreadPool.create(4, 4, 512, "BroadcastVote-");
    }

    @Override
    public void start() {

    }

    public List<RequestVoteRet> broadcastRequestVote() {
        List<RequestVoteRet> requestVoteRets = new ArrayList<>();
        String myNodeId = node.getNodeId();
        Set<String> nodeIdSet = RaftNetwork.clusterNodeIds(myNodeId);
        RequestVoteReq req = RequestVoteReq.build(node.currentTerm(), myNodeId, 0, 0);
        nodeIdSet.parallelStream().forEach(nodeId -> {
            RequestVoteRet ret = send(nodeId, req);
            requestVoteRets.add(ret);
        });
        return requestVoteRets;
    }

    @Override
    public void close() {
        broadcastExecutor.shutdownNow();
    }

    @Override
    public RequestVoteRet send(String nodeId, RequestVoteReq requestVoteReq) {
        return RpcService.getInstance().requestVote(nodeId, requestVoteReq);
    }
}
