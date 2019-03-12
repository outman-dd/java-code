package code.distribution.raft.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈附加日志 RPC 返回值〉<p>
 返回值	解释
 term	当前的任期号，用于领导人去更新自己
 success	跟随者包含了匹配上 prevLogIndex 和 prevLogTerm 的日志时为真
 *
 * @author zixiao
 * @date 2019/3/11
 */
@Data
@AllArgsConstructor
public class AppendEntriesRet implements Serializable{

    /**
     * 当前的任期号，用于领导人去更新自己
     */
    private int term;

    /**
     * 跟随者包含了匹配上 prevLogIndex 和 prevLogTerm 的日志时为真
     */
    private boolean success;


    public static AppendEntriesRet success(int term){
        return new AppendEntriesRet(term, true);
    }

    public static AppendEntriesRet fail(int term){
        return new AppendEntriesRet(term, false);
    }
}
