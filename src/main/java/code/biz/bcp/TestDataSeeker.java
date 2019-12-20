package code.biz.bcp;

import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/3
 */
public class TestDataSeeker implements DataSeeker {

    private Extractor extractor = new TestExtractor();

    private Transformer transformer = new TestTransformer();

    @Override
    public Map<String, Object> seek(Map<String, Object> left) {
        return transformer.transform(extractor.extract(left));
    }

}
