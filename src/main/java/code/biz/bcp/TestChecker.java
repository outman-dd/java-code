package code.biz.bcp;

import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/3
 */
public class TestChecker implements Checker {

    @Override
    public boolean check(Map<String, Object> left, Map<String, Object> right) {
        if(right == null){
            return false;
        }
        return true;
    }

}
