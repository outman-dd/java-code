package code.distribution.raft;

import code.distribution.raft.election.ElectionService;
import code.distribution.raft.election.RequestVoteHandler;
import code.distribution.raft.log.AppendEntriesHandler;
import code.distribution.raft.log.AppendEntriesSender;
import code.distribution.raft.model.AppendEntriesReq;
import code.distribution.raft.model.AppendEntriesRet;
import code.distribution.raft.model.RequestVoteReq;
import code.distribution.raft.model.RequestVoteRet;
import code.distribution.raft.util.RaftLogger;

/**
 * 〈Raft节点服务器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class RaftNodeServer implements IService{

    private final RaftLogger logger;

    private final RaftNode node;

    private final IHandler<RequestVoteReq, RequestVoteRet> requestVoteHandler;

    private final IHandler<AppendEntriesReq, AppendEntriesRet> appendEntriesHandler;

    private final ElectionService electionService;

    //private final HeartbeatSender heartbeatSender;

    private final AppendEntriesSender appendEntriesSender;

    public RaftNodeServer(String nodeId) {
        this.node = new RaftNode(nodeId);
        this.requestVoteHandler = new RequestVoteHandler(node);
        this.appendEntriesHandler = new AppendEntriesHandler(node, this);
        this.electionService = new ElectionService(node, this);
        //this.heartbeatSender = new HeartbeatSender(node);
        this.appendEntriesSender = new AppendEntriesSender(node, this);
        this.logger = RaftLogger.getLogger(nodeId);
    }

    @Override
    public void start() {
        logger.info("start...");
        RaftNetwork.register(node.getNodeId(), this);
        this.electionService.start();
    }

    @Override
    public void close() {
        logger.info("offline...");
        RaftNetwork.offline(node.getNodeId());
        this.electionService.close();
        //this.heartbeatSender.close();
        this.appendEntriesSender.close();
    }

    public RequestVoteRet handleRequestVote(RequestVoteReq req){
        return requestVoteHandler.handle(req);
    }

    public AppendEntriesRet handleAppendEntries(AppendEntriesReq req){
        return appendEntriesHandler.handle(req);
    }

    public void resetElectionTimeout(){
        this.electionService.resetElectionTimeout();
    }

    public void changeToLeader(){
        node.changeToLeader();
        //heartbeatSender.start();
        appendEntriesSender.start();
        electionService.pauseElection();
    }

    public void changeToFollower(int newTerm){
        node.getCurrentTerm().set(newTerm);
        node.changeToFollower();
        //heartbeatSender.close();
        appendEntriesSender.close();
    }

    public RaftNode getNode() {
        return node;
    }
}
