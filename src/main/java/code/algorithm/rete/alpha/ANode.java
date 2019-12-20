package code.algorithm.rete.alpha;

import code.algorithm.rete.pattern.GradeGt3Matcher;

/**
 * 〈年级是三年级以上〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class ANode extends AlphaNode {

    private static ANode instance = new ANode();

    public static ANode getInstance() {
        return instance;
    }

    public ANode() {
        super(new GradeGt3Matcher(), null);
    }

}
