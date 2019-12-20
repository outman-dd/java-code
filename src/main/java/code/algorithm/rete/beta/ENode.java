package code.algorithm.rete.beta;

/**
 * 〈左节点为C〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class ENode extends BetaNode{

    private static ENode instance = new ENode();

    public static ENode getInstance() {
        return instance;
    }

    protected ENode() {
        super(CNode.getInstance());
    }
}
