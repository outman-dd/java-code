package code.kv;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈数据〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/9
 */
@Data
@AllArgsConstructor
public class KvData implements Serializable{

    private String bizId;

    private String bizType;

    private String modelCode;

    private String fieldCode;

    private String value;

    private Integer seq;

}
