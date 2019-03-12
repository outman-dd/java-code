package code.distribution.raft.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈请求投票 RPC 返回值〉<p>
 返回值	解释
 term	当前任期号，以便于候选人去更新自己的任期号
 voteGranted	候选人赢得了此张选票时为真
 *
 * @author zixiao
 * @date 2019/3/11
 */
@Data
@AllArgsConstructor
public class RequestVoteRet implements Serializable{

    /**
     * 当前任期号，以便于候选人去更新自己的任期号
     */
    private int term;

    /**
     * 候选人赢得了此张选票时为真
     */
    private boolean voteGranted;

    public static RequestVoteRet accept(int term){
        return new RequestVoteRet(term, true);
    }

    public static RequestVoteRet reject(int term){
        return new RequestVoteRet(term, false);
    }

}
