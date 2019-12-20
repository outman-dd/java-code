package code.algorithm.rete.alpha;

import code.algorithm.rete.beta.GNode;
import code.algorithm.rete.pattern.HeightGt170Matcher;

/**
 * 〈身高170cm以上〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class FNode extends AlphaNode {

    private static FNode instance = new FNode();

    public static FNode getInstance() {
        return instance;
    }

    protected FNode() {
        super(new HeightGt170Matcher(), GNode.getInstance());
    }
}
