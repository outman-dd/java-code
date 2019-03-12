package code.distribution.raft.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈被投票者〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/12
 */
@Data
@AllArgsConstructor
public class VoteFor implements Serializable{

    /**
     * 被投票 nodeId
     */
    private String nodeId;

    /**
     * 得票所在任期
     */
    private int term;
}
