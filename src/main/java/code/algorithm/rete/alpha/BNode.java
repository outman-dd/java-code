package code.algorithm.rete.alpha;

import code.algorithm.rete.beta.CNode;
import code.algorithm.rete.pattern.GenderIsBoyMatcher;

/**
 * 〈性别是男的〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class BNode extends AlphaNode {

    private static BNode instance = new BNode();

    public static BNode getInstance() {
        return instance;
    }

    public BNode() {
        super(new GenderIsBoyMatcher(), CNode.getInstance());
    }

}
