package code.distribution.raft;

import code.distribution.raft.enums.RoleType;
import code.distribution.raft.model.EntryIndex;
import code.distribution.raft.model.LogEntry;
import code.distribution.raft.model.VoteFor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
    private List<LogEntry> logs;

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
    private transient List<EntryIndex> nextIndex = new ArrayList<>(32);

    /**
     * 对于每一个服务器，已经复制给他的日志的最高索引值
     */
    private transient List<EntryIndex> matchIndex = new ArrayList<>(32);

    private transient ReentrantReadWriteLock logLock = new ReentrantReadWriteLock();

    private transient ReentrantLock voteLock = new ReentrantLock();

    public RaftNode(String nodeId) {
        this.nodeId = nodeId;
        this.role = RoleType.FOLLOWER;
        this.currentTerm = new AtomicInteger(0);
        this.logs = new CopyOnWriteArrayList<>();
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

    public void initNextIndex(){
        nextIndex.clear();
        Set<String> nodeIdSet = RaftNetwork.clusterNodeIds(nodeId);
        int nextIdx = commitIndex.get() + 1;
        nodeIdSet.forEach(nodeId -> {
            nextIndex.add(new EntryIndex(nodeId, nextIdx));
        });
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

    /*************************************** logEntry ***************************************/
    public LogEntry logIndexOf(int logIndex){
        logLock.readLock().lock();
        try {
            if(logs.size() > logIndex){
                return logs.get(logIndex);
            }else{
                return null;
            }
        }finally {
            logLock.readLock().unlock();
        }
    }

    public void logRemoveFrom(int fromIndex){
        logLock.writeLock().lock();
        try{
            if(logs.size() > fromIndex){
                Iterator<LogEntry> iterator = logs.iterator();
                int startIndex = 0;
                while (iterator.hasNext()){
                    iterator.next();
                    if(startIndex >= fromIndex){
                        iterator.remove();
                    }
                    startIndex++;
                }
            }
        }finally {
            logLock.writeLock().unlock();
        }
    }

    public void logAppend(int index, LogEntry entry) {
        logLock.writeLock().lock();
        try{
            logs.add(index, entry);
        }finally {
            logLock.writeLock().unlock();
        }
    }

    public Pair<Integer, LogEntry> lastLog(){
        logLock.readLock().lock();
        try {
            if(logs.size() == 0){
                return null;
            }else{
                int lastIndex = logs.size()-1;
                return Pair.of(lastIndex, logs.get(lastIndex));
            }
        }finally {
            logLock.readLock().unlock();
        }
    }

}
