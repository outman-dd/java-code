package code.distribution.raft.log;

import code.distribution.raft.IHandler;
import code.distribution.raft.RaftNode;
import code.distribution.raft.RaftNodeServer;
import code.distribution.raft.model.AppendEntriesReq;
import code.distribution.raft.model.AppendEntriesRet;
import code.distribution.raft.model.LogEntry;
import code.distribution.raft.util.RaftLogger;

/**
 * 〈附加日志 处理器〉<p>
 接收者实现：

 1、如果 term < currentTerm 就返回 false （5.1 节）
 2、如果日志在 prevLogIndex 位置处的日志条目的任期号和 prevLogTerm 不匹配，则返回 false
 3、如果已经存在的日志条目和新的产生冲突（索引值相同但是任期号不同），删除这一条和之后所有的
 4、附加日志中尚未存在的任何新条目
 5、如果 leaderCommit > commitIndex，令 commitIndex 等于 leaderCommit 和新日志条目索引值 中较小的一个
 *
 * @author zixiao
 * @date 2019/3/11
 */
public class AppendEntriesHandler implements IHandler<AppendEntriesReq, AppendEntriesRet> {

    private final RaftLogger logger;

    private final RaftNode node;

    private final RaftNodeServer nodeServer;

    public AppendEntriesHandler(RaftNode node, RaftNodeServer nodeServer) {
        this.node = node;
        this.nodeServer = nodeServer;
        this.logger = RaftLogger.getLogger(node.getNodeId());
    }

    @Override
    public AppendEntriesRet handle(AppendEntriesReq appendEntriesReq) {
        //1.1、如果收到的任期比当前任期小,返回false
        int currentTerm = node.currentTerm();
        if(appendEntriesReq.getTerm() < currentTerm){
            return AppendEntriesRet.fail(currentTerm);
        //1.2如果RPC请求或者响应包含的任期T > currentTerm，将currentTerm设置为T并转换为Follower
        } else if(appendEntriesReq.getTerm() > currentTerm){
            node.getCurrentTerm().compareAndSet(currentTerm, appendEntriesReq.getTerm());
            currentTerm = node.currentTerm();
        }

        //1.3收到Leader节点的AppendEntries请求，转换为Follower节点
        node.changeToFollower();

        //2、重置选择超时时间
        nodeServer.resetElectionTimeout();

        //3、处理日志复制
        if (appendEntriesReq.getEntries() == null){
            return heartbeat(appendEntriesReq, currentTerm);
        }
        return appendEntries(appendEntriesReq, currentTerm);
    }

    /**
     * 处理心跳
     * @param appendEntriesReq
     * @param currentTerm
     * @return
     */
    private AppendEntriesRet heartbeat(AppendEntriesReq appendEntriesReq, int currentTerm) {
        logger.info("Get heartbeat from leader {0}", appendEntriesReq.getLeaderId());
        return AppendEntriesRet.success(currentTerm);
    }

    /**
     * 附加日志，接收者实现：
     1、如果收到的任期比当前任期小,返回false
     2、如果不包含之前的日志条目（没有匹配 prevLogIndex 和 prevLogTerm）,返回false
     3、如果存在index相同，但是term不相同的日志，删除从该位置开始所有的日志
     4、追加所有不存在的日志
     5、如果leaderCommit>commitIndex，将commitIndex设置为commitIndex = min(leaderCommit, index of last new entry)
     * @param appendEntriesReq
     * @param currentTerm
     * @return
     */
    private AppendEntriesRet appendEntries(AppendEntriesReq appendEntriesReq, int currentTerm){
        logger.info("Get append entries request from leader {0}", appendEntriesReq.getLeaderId());

        //2、如果不包含之前的日志条目（没有匹配 prevLogIndex 和 prevLogTerm）,返回false
        if(appendEntriesReq.getPrevLogIndex() >= 0){
            LogEntry prevLog = node.logIndexOf(appendEntriesReq.getPrevLogIndex());
            if(prevLog == null && prevLog.getTerm() != appendEntriesReq.getPrevLogTerm()){
                return AppendEntriesRet.fail(currentTerm);
            }
        }else{
            //第一条日志，没有preLog
        }

        //3、如果存在index相同，但是term不相同的日志，删除从该位置开始所有的日志
        //4、追加所有不存在的日志
        LogEntry[] entries = appendEntriesReq.getEntries();
        int idx = appendEntriesReq.getPrevLogIndex();
        for (LogEntry entry : entries){
            idx++;
            LogEntry existedLog = node.logIndexOf(idx);
            if(existedLog != null && entry.getTerm() != existedLog.getTerm()){
                removeEntriesFrom(idx);
            }else{
                appendEntry(idx, entry);
            }
        }

        //5、如果leaderCommit>commitIndex，将commitIndex设置为commitIndex = min(leaderCommit, index of last new entry)
        int leaderCommit = appendEntriesReq.getLeaderCommit();
        if(leaderCommit > node.getCommitIndex().get()){
            node.setCommitIndex(Math.min(leaderCommit, idx));
        }
        return AppendEntriesRet.success(currentTerm);
    }

    private void appendEntry(int index, LogEntry entry) {
        node.logAppend(index, entry);
    }

    private void removeEntriesFrom(int fromIndex){
        node.logRemoveFrom(fromIndex);
    }



}
