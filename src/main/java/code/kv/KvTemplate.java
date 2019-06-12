package code.kv;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈KvTemplate〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/9
 */
@Data
@AllArgsConstructor
public class KvTemplate implements Serializable{

    private int id;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 字段编号
     */
    private String fieldCode;

    /**
     * 字段类型
     */
    private DataType dataType;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 非空白（针对字符串）
     */
    private boolean notBlank;

    /**
     * 最大长度
     */
    private int maxLength;

    /**
     * 正则表达式
     */
    private String regex;

    public KvTemplate(String biType, String fieldCode,  DataType dataType, boolean required) {
        this.bizType = biType;
        this.fieldCode = fieldCode;
        this.dataType = dataType;
        this.required = required;
    }
}
