package code.algorithm.rete.rule;

import code.algorithm.rete.fact.Fact;

/**
 * 〈规则〉<p>
 * IF LHS
 * THEN RHS
 *
 * @author zixiao
 * @date 2019/8/27
 */
public interface Rule {

    String execute(Fact fact);

}
