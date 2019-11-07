package code.distribution.raft.kv;

import code.distribution.raft.RaftConst;
import code.distribution.raft.fsm.StateMachine;
import code.distribution.raft.model.LogEntry;

import java.util.HashMap;
import java.util.Map;

import static code.distribution.raft.kv.KvOpType.DEL;
import static code.distribution.raft.kv.KvOpType.SET;
import static java.lang.System.getProperty;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-04
 */
public class KvStateMachine implements StateMachine {

    private Map<String, String> kvMap = new HashMap<>();

    @Override
    public void apply(LogEntry logEntry) {
        KvCommand kvCommand = (KvCommand) logEntry.getCommand();

        if (SET == kvCommand.getOpType()) {
            setString(kvCommand.getKey(), kvCommand.getValue());
        } else if (DEL == kvCommand.getOpType()) {
            delString(kvCommand.getKey());
        }
    }

    @Override
    public String getString(String key) {
        return kvMap.get(key);
    }

    @Override
    public void setString(String key, String value) {
        kvMap.put(key, value);
    }

    @Override
    public void delString(String key) {
        kvMap.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : kvMap.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=>").append(entry.getValue()).append(RaftConst.LINE_SEP);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        StateMachine stateMachine = new KvStateMachine();
        stateMachine.setString("a", "1");
        stateMachine.setString("b", "2");
    }

}
