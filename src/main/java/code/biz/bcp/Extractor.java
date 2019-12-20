package code.biz.bcp;

import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/3
 */
public interface Extractor {

    Map<String, Object> extract(Map<String, Object> param);
}
