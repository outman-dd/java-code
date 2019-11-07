package code.distribution.raft.election;

import code.distribution.raft.*;
import code.distribution.raft.model.RequestVoteRet;
import code.util.NamedThreadFactory;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ElectionService.class);

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

    public void resetElectionTimeout() {
        this.electionTimer.getQueue().clear();
        this.pause = false;

        long electionTimeOut = getRandomElectionTimeout();
        this.electionTimer.scheduleWithFixedDelay(new ElectionTask(), electionTimeOut, electionTimeOut, TimeUnit.MILLISECONDS);
    }

    /**
     * 开始选择
     * 1、当前term自增1
     * 2、身份切换为Candidate
     * 3、投票给自己
     * 4、广播投票, 发送 RequestVote 消息(带上currentTerm)给其它所有server
     */
    private class ElectionTask implements Runnable {

        @Override
        public void run() {
            if (pause || !inElection.compareAndSet(false, true)) {
                return;
            }
            try {
                //1、当前term自增1
                int currentTerm = node.getCurrentTerm().incrementAndGet();
                LOGGER.info("start election, term={}", currentTerm);

                //2、身份切换为Candidate
                node.setLeader(null);
                node.changeToCandidate();

                //3、投票给自己
                boolean success = node.voteFor(node.getNodeId(), currentTerm);

                //4、广播投票, 发送 RequestVote 消息(带上currentTerm)给其它所有server
                if (success) {
                    LOGGER.info("start broadcastRequestVote, term={}", currentTerm);
                    List<RequestVoteRet> voteRetList = requestVoteSender.broadcastRequestVote();

                    //处理投票结果 TODO 改异步执行
                    handleVoteResult(voteRetList);
                } else {
                    //被其他candidate捷足先登了
                    LOGGER.info("Can not vote to self, term={}", currentTerm);
                }
            } finally {
                inElection.compareAndSet(true, false);
            }
        }
    }

    private long getRandomElectionTimeout() {
        return RaftConst.ELECTION_TIMEOUT_MS + RandomUtils.nextLong(0, RaftConst.ELECTION_TIMEOUT_MS);
    }

    private void handleVoteResult(List<RequestVoteRet> voteRetList) {
        AtomicInteger votesNum = new AtomicInteger(1);
        int maxTerm = node.currentTerm();
        for (RequestVoteRet requestVoteRet : voteRetList) {
            if (requestVoteRet == null) {
                //Vote rpc fail
                continue;
            }
            if (requestVoteRet.isVoteGranted()) {
                votesNum.incrementAndGet();
            } else if (requestVoteRet.getTerm() > node.currentTerm()) {
                maxTerm = requestVoteRet.getTerm();
            }
        }
        //如果RPC请求或者响应包含的任期T > currentTerm，将currentTerm设置为T并转换为Follower
        if (maxTerm > node.currentTerm()) {
            LOGGER.info("Someone 's term {} is greater then mime {}", maxTerm, node.getCurrentTerm());
            nodeServer.changeToFollower(maxTerm);
            return;
        }
        //多数派原则，当选leader
        if (votesNum.get() > RaftNetwork.nodeNum() / 2) {
            LOGGER.info("I get most votes ({}) and become the leader in term {}", votesNum.get(), node.currentTerm());
            nodeServer.changeToLeader();
        } else {
            LOGGER.info("Num of votes ({}) that I get is less than (nodesNum/2+1), nodesNum={}", votesNum.get(), RaftNetwork.nodeNum());
        }
    }

    /**
     * 暂停选举服务，（当选leader后）
     */
    public void pauseElection() {
        pause = true;
    }

}
