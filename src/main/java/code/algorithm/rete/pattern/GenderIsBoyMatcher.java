package code.algorithm.rete.pattern;

/**
 * 〈性别是男的〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class GenderIsBoyMatcher implements PatternMatcher {

    @Override
    public boolean match(Object value) {
        return "M".equals(value.toString());
    }

}
