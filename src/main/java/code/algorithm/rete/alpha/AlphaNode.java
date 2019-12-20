package code.algorithm.rete.alpha;

import code.algorithm.rete.fact.Fact;
import code.algorithm.rete.node.Node;
import code.algorithm.rete.pattern.PatternMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * 〈AlphaNode〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public abstract class AlphaNode implements Node {

    /**
     * alpha内存区
     */
    private Set<Fact> memory = new HashSet<>();

    private final PatternMatcher matcher;

    private final Node leftNode;

    protected AlphaNode(PatternMatcher matcher, Node leftNode) {
        this.matcher = matcher;
        this.leftNode = leftNode;
    }

    @Override
    public Node getLeft() {
        return leftNode;
    }

    public boolean match(Fact fact, Object value) {
        //从alpha节点模式匹配
        boolean match = matcher.match(value);
        if (match) {
            //找到加入alpha内存
            putMemory(fact);
            return loopExist(leftNode, fact);
        }

        return match;
    }

    /**
     * @param left
     * @param fact
     * @return
     */
    private boolean loopExist(Node left, Fact fact) {
        //没有左节点，返回存在
        if (left == null) {
            return true;
        }

        //左节点中存在该事实，返回存在
        if (left.inMemory(fact)) {
            return true;
        }

        //左节点中不存在该事实，且左节点（它是alpha节点）没有左节点，则返回不存在
        Node leftLeft = left.getLeft();
        if (leftLeft == null) {
            return false;
        }

        //如果左节点的左节点存在该事实，则放入把左节点，返回存在
        if (leftLeft.inMemory(fact)) {
            left.putMemory(fact);
            return true;
        } else {
            //返回不存在
            return false;
        }
    }

    public boolean inMemory(Fact fact) {
        System.out.println(fact + " in Alpha");
        return memory.contains(fact);
    }

    public void putMemory(Fact fact) {
        memory.add(fact);
    }

}
