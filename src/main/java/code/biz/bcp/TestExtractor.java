package code.biz.bcp;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/3
 */
public class TestExtractor implements Extractor {

    @Override
    public Map<String, Object> extract(Map<String, Object> param) {
        if((Long)param.get("loanId") < 0){
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("loanId", param.get("loanId"));
        return map;
    }

}
