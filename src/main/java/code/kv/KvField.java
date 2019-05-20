package code.kv;

import lombok.Data;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/9
 */
@Data
public class KvField implements Serializable{

    private int id;

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private DataType dataType;

    /**
     * 必填
     */
    private boolean required;

}
