package code.algorithm.rete.pattern;

/**
 * 〈年龄小于10岁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class AgeLt10Matcher implements PatternMatcher {

    @Override
    public boolean match(Object value) {
        return Integer.valueOf(value.toString()) < 10;
    }

}
