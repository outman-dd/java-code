package code.distribution.raft.rpc;

import code.distribution.raft.RaftNetwork;
import code.distribution.raft.RaftNodeServer;
import code.distribution.raft.model.AppendEntriesReq;
import code.distribution.raft.model.AppendEntriesRet;
import code.distribution.raft.model.RequestVoteReq;
import code.distribution.raft.model.RequestVoteRet;
import org.apache.commons.lang3.RandomUtils;

/**
 * 〈RpcService〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/7
 */
public class RpcService {

    private static RpcService INST = new RpcService();

    private RpcService(){}

    public static RpcService getInstance(){
        return INST;
    }

    public RequestVoteRet requestVote(String nodeId, RequestVoteReq req) {
        RaftNodeServer nodeServer = RaftNetwork.nodeServer(nodeId);
        randomNetworkLatency();
        return nodeServer.handleRequestVote(req);
    }

    public AppendEntriesRet appendEntries(String nodeId, AppendEntriesReq req){
        RaftNodeServer nodeServer = RaftNetwork.nodeServer(nodeId);
        randomNetworkLatency();
        return nodeServer.handleAppendEntries(req);
    }

    private void randomNetworkLatency(){
        try {
            Thread.sleep(RandomUtils.nextInt(1, 3));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
