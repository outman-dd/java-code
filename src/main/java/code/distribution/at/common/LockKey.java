package code.distribution.at.common;

import lombok.Data;

import java.util.List;

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

    private List<String> pkValues;

    public LockKey(ArgContext argContext) {
        this(argContext.getTableName(), argContext.getPkValues());
    }

    public LockKey(String tableName, List<String> pkValues) {
        this.tableName = tableName;
        this.pkValues = pkValues;
    }

    @Override
    public LockKey clone(){
        return new LockKey(this.getTableName(), this.getPkValues());
    }

}
