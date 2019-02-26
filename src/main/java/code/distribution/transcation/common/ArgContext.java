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
public class ArgContext {

    private String bizType;

    private String tableName;

    private String keyName;

    private Object keyValue;

    private Object[] args;

    public ArgContext(String bizType, String tableName, String keyName, Object keyValue, Object[] args) {
        this.bizType = bizType;
        this.tableName = tableName;
        this.keyName = keyName;
        this.keyValue = keyValue;
        this.args = args;
    }
}
