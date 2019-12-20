package code.algorithm.rete.beta;

import code.algorithm.rete.alpha.ANode;

/**
 * 〈年级是三年级以上 & 性别是男的〉<p>
 * 〈左节点为A〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class CNode extends BetaNode{

    private static CNode instance = new CNode();

    public static CNode getInstance() {
        return instance;
    }

    protected CNode() {
        super(ANode.getInstance());
    }

}
