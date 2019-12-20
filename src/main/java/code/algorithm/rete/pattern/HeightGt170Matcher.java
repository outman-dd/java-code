package code.algorithm.rete.pattern;

/**
 * 〈身高170cm以上〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class HeightGt170Matcher implements PatternMatcher{

    @Override
    public boolean match(Object value) {
        return Integer.valueOf(value.toString()) > 170;
    }
}
