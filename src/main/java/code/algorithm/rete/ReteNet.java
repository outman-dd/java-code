package code.algorithm.rete;

import code.algorithm.rete.alpha.ANode;
import code.algorithm.rete.alpha.BNode;
import code.algorithm.rete.alpha.DNode;
import code.algorithm.rete.alpha.FNode;
import code.algorithm.rete.beta.GNode;
import code.algorithm.rete.fact.Fact;
import code.algorithm.rete.fact.StudentFact;
import code.algorithm.rete.rule.Rule;

/**
 * 〈推理网络〉<p>
 * 〈功能详细描述〉
 *                                  Root Node
 *                                      |
 *  TypeNode:     [Grade]     [Gender]      [Age]        [Height]
 *                   |           |            |             |
 *  AlphaNode:   A(grade>3)   B(gender=M)  D(age<10)      F(height>170)
 *                  |        |            |              |
 *  BetaNode:        C(grade>3,gender=M)  |              |
 *                           |            |              |
 *                            E(grade>3,gender=M,age<10) |
 *                                                 |     |
 *                                                  G(grade>3,gender=M,age<10,height>170)
 *                                                       |
 *  Agenda:                                        该学生是一个篮球苗子
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class ReteNet {

    public String execute(Rule rule, Fact fact){
        return rule.execute(fact);
    }

}
