package code.distribution.raft;

import code.distribution.raft.client.ClientReq;
import code.distribution.raft.client.ClientRet;
import code.distribution.raft.election.ElectionService;
import code.distribution.raft.election.RequestVoteHandler;
import code.distribution.raft.enums.RoleType;
import code.distribution.raft.fsm.StateMachine;
import code.distribution.raft.kv.KvCommand;
import code.distribution.raft.log.AppendEntriesHandler;
import code.distribution.raft.log.AppendEntriesSender;
import code.distribution.raft.model.*;
import code.distribution.raft.rpc.HttpNettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈Raft节点服务器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class RaftNodeServer implements IService{

    private static final Logger LOGGER = LoggerFactory.getLogger(RaftNodeServer.class);

    private final RaftNode node;

    private final IHandler<RequestVoteReq, RequestVoteRet> requestVoteHandler;

    private final IHandler<AppendEntriesReq, AppendEntriesRet> appendEntriesHandler;

    private final ElectionService electionService;

    private final AppendEntriesSender appendEntriesSender;

    private final HttpNettyServer httpNettyServer;

    public RaftNodeServer(String nodeId, StateMachine stateMachine) {
        this.node = new RaftNode(nodeId, stateMachine);
        this.requestVoteHandler = new RequestVoteHandler(node, this);
        this.appendEntriesHandler = new AppendEntriesHandler(node, this);
        this.electionService = new ElectionService(node, this);
        this.appendEntriesSender = new AppendEntriesSender(node, this);
        this.httpNettyServer = new HttpNettyServer(this);
    }

    public void initConfig(RaftConfig raftConfig){
        RaftNetwork.config(raftConfig.parseClusterNodes());
    }

    @Override
    public void start() {
        LOGGER.info("Node {} start...", node.getNodeId());
        electionService.start();
        //必须放最后
        httpNettyServer.start();
    }

    @Override
    public void close() {
        LOGGER.info("Node {} stop...", node.getNodeId());
        electionService.close();
        appendEntriesSender.close();
        httpNettyServer.destroy();
        //保存快照
        node.saveSnapshot();
    }

    public RequestVoteRet handleRequestVote(RequestVoteReq req){
        return requestVoteHandler.handle(req);
    }

    public AppendEntriesRet handleAppendEntries(AppendEntriesReq req){
        return appendEntriesHandler.handle(req);
    }

    public ClientRet handleClientRequest(ClientReq clientReq){
        if(node.getRole() == RoleType.FOLLOWER){
            return ClientRet.buildRedirect(node.getLeaderId());
        }else if(node.getRole() == RoleType.CANDIDATE){
            return ClientRet.buildRedirect(null);
        }

        if(clientReq.isRead()){
            String key = ((KvCommand)clientReq.getCommand()).getKey();
            String value = node.getStateMachine().getString(key);
            return ClientRet.buildSuccess(value);
        }else{
            boolean appendSuccess = appendEntriesSender.appendEntries(clientReq.getCommand());
            return ClientRet.build(appendSuccess);
        }
    }

    public void resetElectionTimeout(){
        this.electionService.resetElectionTimeout();
    }

    public void changeToLeader(){
        node.changeToLeader();
        appendEntriesSender.start();
        electionService.pauseElection();
    }

    public void changeToFollower(int newTerm){
        node.getCurrentTerm().set(newTerm);
        node.changeToFollower();
        appendEntriesSender.close();
    }

    public RaftNode getNode() {
        return node;
    }
}
