package code.algorithm.rete.beta;

import code.algorithm.rete.fact.Fact;
import code.algorithm.rete.node.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * 〈BetaNode〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public abstract class BetaNode implements Node{

    private Set<Fact> memory = new HashSet<>();

    private final Node leftNode;

    protected BetaNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    @Override
    public Node getLeft(){
        return leftNode;
    }

    public boolean inMemory(Fact fact) {
        System.out.println(fact + " in Beta");
        return memory.contains(fact);
    }

    public void putMemory(Fact fact) {
        memory.add(fact);
    }

    public Set<Fact> getMemory() {
        return memory;
    }
}
