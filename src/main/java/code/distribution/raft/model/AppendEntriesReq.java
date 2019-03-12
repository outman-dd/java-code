package code.distribution.raft.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈附加日志 RPC 请求参数〉<p>

 由领导人负责调用来复制日志指令；也会用作heartbeat

 参数	解释
 term	领导人的任期号
 leaderId	领导人的 Id，以便于跟随者重定向请求
 prevLogIndex	新的日志条目紧随之前的索引值
 prevLogTerm	prevLogIndex 条目的任期号
 entries[]	准备存储的日志条目（表示心跳时为空；一次性发送多个是为了提高效率）
 leaderCommit	领导人已经提交的日志的索引值
 *
 * @author zixiao
 * @date 2019/3/11
 */
@Data
@AllArgsConstructor
public class AppendEntriesReq implements Serializable{

    /**
     * 领导人的任期号
     */
    private int term;

    /**
     * 领导人的 Id，以便于跟随者重定向请求
     */
    private String leaderId;

    /**
     * 新的日志条目紧随之前的索引值
     */
    private int prevLogIndex;

    /**
     * prevLogIndex 条目的任期号
     */
    private int prevLogTerm;

    /**
     * 准备存储的日志条目
     * （表示心跳时为空；一次性发送多个是为了提高效率）
     */
    private LogEntry[] entries;

    /**
     * 领导人已经提交的日志的索引值
     */
    private int leaderCommit;

    public AppendEntriesReq(int term, String leaderId) {
        this.term = term;
        this.leaderId = leaderId;
    }

    public static AppendEntriesReq buildHeartbeat(int term, String leaderId){
        return new AppendEntriesReq(term, leaderId);
    }

    public static AppendEntriesReq build(int term, String leaderId, int prevLogIndex, int prevLogTerm,
                                         LogEntry[] entries, int leaderCommit){
        return new AppendEntriesReq(term, leaderId, prevLogIndex, prevLogTerm, entries, leaderCommit);
    }

}
