package code.algorithm.rete.pattern;

/**
 * 〈年级是三年级以上〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class GradeGt3Matcher implements PatternMatcher {

    @Override
    public boolean match(Object value) {
        return Integer.valueOf(value.toString()) > 3;
    }

}
