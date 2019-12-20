package code.algorithm.rete.node;

import code.algorithm.rete.fact.Fact;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public interface Node {

    Node getLeft();

    boolean inMemory(Fact fact);

    void putMemory(Fact fact);

}
