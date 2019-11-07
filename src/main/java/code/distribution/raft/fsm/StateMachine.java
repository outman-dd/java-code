package code.distribution.raft.fsm;

import code.distribution.raft.model.LogEntry;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-04
 */
public interface StateMachine {

    void apply(LogEntry logEntry);

    String getString(String key);

    void setString(String key, String value);

    void delString(String key);

}
