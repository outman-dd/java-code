package code.biz.bcp;

import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/3
 */
public interface Transformer {

    Map<String, Object> transform(Map<String, Object> source);
}
