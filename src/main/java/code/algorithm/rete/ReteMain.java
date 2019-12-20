package code.algorithm.rete;

import code.algorithm.rete.fact.StudentFact;
import code.algorithm.rete.rule.BasketballRule;

/**
 * 〈一句话功能简述〉<p>
 IF：
     年级是三年级以上，
     性别是男的，
     年龄小于10岁，
     身高170cm以上，
 THEN：
     该学生是一个篮球苗子
 *
 * @author zixiao
 * @date 2019/8/27
 */
public class ReteMain {

    public static void main(String[] args) {
        StudentFact studentFact = new StudentFact();
        studentFact.setName("姚明儿子");
        studentFact.setGrade(4);
        studentFact.setGender("M");
        studentFact.setAge(9);
        studentFact.setHeight(172);

        ReteNet reteNet = new ReteNet();
        System.out.println(reteNet.execute(new BasketballRule(), studentFact));
    }
}
