package code.distribution.raft.election;

import code.distribution.raft.IService;
import code.distribution.raft.RaftConst;
import code.distribution.raft.RaftNetwork;
import code.distribution.raft.RaftNode;
import code.distribution.raft.RaftNodeServer;
import code.distribution.raft.model.RequestVoteRet;
import code.distribution.raft.util.RaftLogger;
import code.util.NamedThreadFactory;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈选举服务〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class ElectionService implements IService {

    private final RaftLogger logger;

    private final RaftNode node;

    private final RaftNodeServer nodeServer;

    private final ScheduledThreadPoolExecutor electionTimer;

    private final AtomicBoolean inElection = new AtomicBoolean();

    /**
     * 是否暂停
     */
    private boolean pause = false;

    private final RequestVoteSender requestVoteSender;

    public ElectionService(RaftNode node, RaftNodeServer nodeServer) {
        this.node = node;
        this.nodeServer = nodeServer;
        this.electionTimer = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("ElectionTimer-"), new RejectedExecutionHandler() {
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
        this.requestVoteSender = new RequestVoteSender(node);
        this.logger = RaftLogger.getLogger(node.getNodeId());
    }

    @Override
    public void start() {
        long electionTimeOut = getRandomElectionTimeout();
        this.electionTimer.scheduleWithFixedDelay(new ElectionTask(), electionTimeOut, electionTimeOut, TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        pauseElection();
        requestVoteSender.close();
    }

    public void resetElectionTimeout(){
        this.electionTimer.getQueue().clear();
        this.pause = false;

        long electionTimeOut = getRandomElectionTimeout();
        this.electionTimer.scheduleWithFixedDelay(new ElectionTask(), electionTimeOut ,electionTimeOut, TimeUnit.MILLISECONDS);
    }

    /**
     * 开始选择
     * 1、当前term自增1
     * 2、身份切换为Candidate
     * 3、投票给自己
     * 4、广播投票, 发送 RequestVote 消息(带上currentTerm)给其它所有server
     */
    private class ElectionTask implements Runnable{

        @Override
        public void run() {
            if(pause || !inElection.compareAndSet(false, true)){
                return;
            }
            try{
                //1、当前term自增1
                int currentTerm = node.getCurrentTerm().incrementAndGet();
                logger.info("start election, term=" + currentTerm);

                //2、身份切换为Candidate
                node.changeToCandidate();

                //3、投票给自己
                boolean success = node.voteFor(node.getNodeId(), currentTerm);

                //4、广播投票, 发送 RequestVote 消息(带上currentTerm)给其它所有server
                if(success){
                    List<RequestVoteRet> voteRetList = requestVoteSender.broadcastRequestVote();

                    //处理投票结果 TODO 改异步执行
                    handleVoteResult(voteRetList);
                }else{
                    //被其他candidate捷足先登了
                }
            }finally {
                inElection.compareAndSet(true, false);
            }
        }
    }

    private long getRandomElectionTimeout(){
        return RaftConst.ELECTION_TIMEOUT_MS + RandomUtils.nextLong(0, RaftConst.ELECTION_TIMEOUT_MS);
    }

    private void handleVoteResult(List<RequestVoteRet> voteRetList){
        AtomicInteger votesNum = new AtomicInteger(1);
        int maxTerm = node.currentTerm();
        for (RequestVoteRet requestVoteRet : voteRetList) {
            if(requestVoteRet.isVoteGranted()){
                votesNum.incrementAndGet();
            }else if(requestVoteRet.getTerm() > node.currentTerm()){
                maxTerm = requestVoteRet.getTerm();
            }
        }
        //如果RPC请求或者响应包含的任期T > currentTerm，将currentTerm设置为T并转换为Follower
        if(maxTerm > node.currentTerm()) {
            logger.info("Someone 's term {0} is greater then mime {1}", maxTerm, node.getCurrentTerm());
            nodeServer.changeToFollower(maxTerm);
            return;
        }
        //多数派原则，当选leader
        if(votesNum.get() > RaftNetwork.nodeNum()/2){
            logger.info("I get most votes ({0}) and become the leader in term {1}", votesNum.get(), node.currentTerm());
            nodeServer.changeToLeader();
        }else{
            logger.info("Num of votes ({0}) that I get is less than (nodesNum/2+1), nodesNum={1}", votesNum.get(), RaftNetwork.nodeNum());
        }
    }

    /**
     * 暂停选举服务，（当选leader后）
     */
    public void pauseElection(){
        pause = true;
    }

}
