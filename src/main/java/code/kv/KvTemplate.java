package code.kv;

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
public class KvTemplate implements Serializable{

    private int id;

    /**
     * 字段编号
     */
    private String fieldCode;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 模型编号
     */
    private String modelCode;

    /**
     * 字段类型
     */
    private DataType dataType;

    /**
     * 必填
     */
    private boolean required;

    /**
     * 非空白（针对字符串）
     */
    boolean notBlank;

    /**
     * 长度（针对字符串）
     */
    int maxLength;

    /**
     * 正则表达式
     */
    String regex;

}
