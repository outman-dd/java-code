package code.distribution.transcation.common;

import lombok.Data;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
@Data
public class LockKey {

    private String tableName;

    private String keyName;

    private Object value;

    public LockKey(ArgContext argContext) {
        this(argContext.getTableName(), argContext.getKeyName(), argContext.getKeyValue());
    }

    public LockKey(String tableName, String keyName, Object value) {
        this.tableName = tableName;
        this.keyName = keyName;
        this.value = value;
    }

    @Override
    public LockKey clone(){
        return new LockKey(this.getTableName(), this.getKeyName(), this.getValue());
    }

}
