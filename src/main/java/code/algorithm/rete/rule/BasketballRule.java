package code.algorithm.rete.rule;

import code.algorithm.rete.alpha.ANode;
import code.algorithm.rete.alpha.BNode;
import code.algorithm.rete.alpha.DNode;
import code.algorithm.rete.alpha.FNode;
import code.algorithm.rete.beta.GNode;
import code.algorithm.rete.fact.Fact;
import code.algorithm.rete.fact.StudentFact;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class BasketballRule implements Rule{

    @Override
    public String execute(Fact fact) {
        StudentFact f = (StudentFact)fact;
        ANode.getInstance().match(f, f.getGrade());
        BNode.getInstance().match(f, f.getGender());
        DNode.getInstance().match(f, f.getAge());
        FNode.getInstance().match(f, f.getHeight());

        GNode.getInstance().getMemory().size();
        StringBuffer sb = new StringBuffer();
        if(GNode.getInstance().getMemory().size() > 0){
            for (Fact fact1 : GNode.getInstance().getMemory()) {
                sb.append(((StudentFact)fact1).getName()).append(",");
            }
            sb.append("是篮球苗子。");
            return sb.toString();
        }else{
            return "没有篮球苗子。";
        }
    }
}
