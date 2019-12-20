package code.biz.bcp;

import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/3
 */
public class TestTransformer implements Transformer{

    @Override
    public Map<String, Object> transform(Map<String, Object> source) {
        if(source == null){
            return null;
        }
        source.put("tag", "1");
        return source;
    }
}
