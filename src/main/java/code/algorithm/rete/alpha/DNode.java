package code.algorithm.rete.alpha;

import code.algorithm.rete.beta.ENode;
import code.algorithm.rete.pattern.AgeLt10Matcher;

/**
 * 〈年龄小于10岁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class DNode extends AlphaNode {

    private static DNode instance = new DNode();

    public static DNode getInstance() {
        return instance;
    }

    public DNode() {
        super(new AgeLt10Matcher(), ENode.getInstance());
    }

}
