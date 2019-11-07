package code.distribution.raft.kv;

import code.distribution.raft.model.Command;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-04
 */
@Data
public class KvCommand implements Command {

    private Integer opType;

    private String key;

    private String value;

    public KvCommand() {
    }

    public KvCommand(Integer opType, String key, String value) {
        this.opType = opType;
        this.key = key;
        this.value = value;
    }

    public static KvCommand buildGet(String key){
        return new KvCommand(KvOpType.GET, key, null);
    }

    public static KvCommand buildSet(String key, String value){
        return new KvCommand(KvOpType.SET, key, value);
    }

    public static KvCommand buildDel(String key){
        return new KvCommand(KvOpType.DEL, key, null);
    }
}
