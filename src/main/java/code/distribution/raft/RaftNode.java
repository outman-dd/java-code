package code.distribution.raft;

import code.distribution.raft.enums.RoleType;
import code.distribution.raft.fsm.StateMachine;
import code.distribution.raft.log.LogModule;
import code.distribution.raft.model.EntryIndex;
import code.distribution.raft.model.LogEntry;
import code.distribution.raft.model.VoteFor;
import code.distribution.raft.util.SnapshotUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 〈Raft节点〉<p>
 *
 状态	所有服务器上持久存在的
 currentTerm	服务器最后一次知道的任期号（初始化为 0，持续递增）
 votedFor	在当前获得选票的候选人的 Id
 log[]	日志条目集；每一个条目包含一个用户状态机执行的指令，和收到时的任期号
 *
 状态	所有服务器上经常变的
 commitIndex	已知的最大的已经被提交的日志条目的索引值
 lastApplied	最后被应用到状态机的日志条目索引值（初始化为 0，持续递增）
 *
 状态	在领导人里经常改变的 （选举后重新初始化）
 nextIndex[]	对于每一个服务器，需要发送给他的下一个日志条目的索引值（初始化为领导人最后索引值加一）
 matchIndex[]	对于每一个服务器，已经复制给他的日志的最高索引值
 *
 * @author zixiao
 * @date 2019/3/11
 */
@Getter
@ToString
@EqualsAndHashCode
public class RaftNode implements Serializable{

    /**
     * 唯一标识
     */
    private final String nodeId;

    /**
     * 角色
     */
    private RoleType role;

    /**
     * 服务器最后一次知道的任期号
     * 初始化为 0，持续递增
     */
    private AtomicInteger currentTerm;

    /**
     * 在当前获得选票的候选人的 Id
     * 投给谁
     */
    private VoteFor voteFor;

    /**
     * 日志条目集；
     * 每一个条目包含一个用户状态机执行的指令，和收到时的任期号
     */
    private LogModule logModule;

    /**
     * 状态机
     */
    private StateMachine stateMachine;

    /**
     * 已知的最大的已经被提交的日志条目的索引值
     */
    private transient AtomicInteger commitIndex;

    /**
     * 最后被应用到状态机的日志条目索引值
     * 初始化为 0，持续递增
     */
    private transient AtomicInteger lastApplied;

    /**
     * 对于每一个服务器，需要发送给他的下一个日志条目的索引值
     * 初始化为领导人最后索引值加一
     */
    private transient Map<String, Integer> nextIndex = new HashMap<>(32);

    /**
     * 对于每一个服务器，已经复制给他的日志的最高索引值
     */
    private transient Map<String, Integer> matchIndex = new HashMap<>(32);

    private String leaderId;

    private transient ReentrantReadWriteLock logLock = new ReentrantReadWriteLock();

    private transient ReentrantLock voteLock = new ReentrantLock();

    public RaftNode(String nodeId, StateMachine stateMachine) {
        this.nodeId = nodeId;
        this.stateMachine = stateMachine;

        this.role = RoleType.FOLLOWER;
        this.currentTerm = new AtomicInteger(0);
        this.logModule = new LogModule();
        this.commitIndex = new AtomicInteger(-1);
        this.lastApplied = new AtomicInteger(-1);
    }

    public int currentTerm(){
        return this.currentTerm.get();
    }

    public void setCommitIndex(int value){
        this.commitIndex.set(value);
    }

    public boolean voteFor(String candidateId, int term){
        voteLock.lock();
        try {
            boolean success = canBeVoteFor(candidateId, term);
            if(success){
                voteFor = new VoteFor(candidateId, term);
            }
            return success;
        }finally {
            voteLock.unlock();
        }
    }

    public boolean canBeVoteFor(String candidateId, int term){
        if(voteFor == null){
            return true;
        }else if(voteFor.getTerm() < term){
            return true;
        }else if(voteFor.getTerm() == term && voteFor.getNodeId().equals(candidateId)){
            return true;
        }
        return false;
    }

    /*************************************** role change ***************************************/

    public void changeToCandidate(){
        if(role == RoleType.FOLLOWER){
            role = RoleType.CANDIDATE;
        }
    }

    public void changeToLeader(){
        if(role == RoleType.CANDIDATE){
            role = RoleType.LEADER;
            initNextIndex();
        }
    }

    public void changeToFollower(){
        role = RoleType.FOLLOWER;
    }

    public void setLeader(String leaderId){
        this.leaderId = leaderId;
    }

    /*************************************** logEntry ***************************************/

    private void initNextIndex(){
        // 每个follower的日志待发送位置初始化为leader最后日志位置+1
        int nextIdx = logModule.lastLogIndex() + 1;
        nextIndex.clear();

        Set<String> nodeIdSet = RaftNetwork.clusterNodeIds(nodeId);
        nodeIdSet.forEach(nodeId -> {
            nextIndex.put(nodeId, nextIdx);
        });
    }

    public void saveSnapshot(){
        SnapshotUtils.save(this);
    }

    /*************************************** state machine ***************************************/

    /**
     * 应用状态机到commitIndex位置为止
     * @param commitIndex
     */
    public void applyTo(int commitIndex){
        synchronized (stateMachine){
            int startApplyIndex = lastApplied.get() + 1;
            List<LogEntry> toApplyLogs = logModule.subLogs(startApplyIndex, commitIndex);
            toApplyLogs.forEach(logEntry -> {
                stateMachine.apply(logEntry);
            });
            lastApplied.set(commitIndex);
        }
    }

    /**
     * 应用状态机 TODO 异步处理
     * @param logEntry
     * @param logIndex
     */
    public void apply(LogEntry logEntry, int logIndex){
        synchronized (stateMachine){
            stateMachine.apply(logEntry);
            lastApplied.set(logIndex);
        }
    }

}
