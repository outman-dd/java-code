package code.algorithm.rete.beta;

/**
 * 〈左节点为E〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class GNode extends BetaNode {

    private static GNode instance = new GNode();

    public static GNode getInstance() {
        return instance;
    }

    protected GNode() {
        super(ENode.getInstance());
    }

}
