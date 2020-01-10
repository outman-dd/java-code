package code.distribution.at.common;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
@Data
public class ArgContext {

    private String tableName;

    private List<String> pkValues = new ArrayList<>(4);

    private Object[] args;

    public ArgContext(String tableName, String pkValue, Object[] args) {
        this.tableName = tableName;
        String[] pkArray = pkValue.split(",");
        for (String pk : pkArray) {
            this.pkValues.add(pk);
        }
        this.args = args;
    }

    public ArgContext(String tableName, List<String> pkValues, Object[] args) {
        this.tableName = tableName;
        this.pkValues = pkValues;
        this.args = args;
    }
}
