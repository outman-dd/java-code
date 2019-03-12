package code.distribution.raft.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈日志条目〉<p>
 * 每一个条目包含一个用户状态机执行的指令，和收到时的任期号
 *
 * @author zixiao
 * @date 2019/3/11
 */
@Data
@AllArgsConstructor
public class LogEntry implements Serializable {

    /**
     * 收到时的任期号
     */
    private int term;

    /**
     * 被复制的用户状态机执行的指令
     */
    private Object command;
}
