package code.algorithm.rete.fact;

import lombok.Data;

/**
 * 〈学生〉<p>
 * 〈事实对象〉
 *
 * @author zixiao
 * @date 2019/8/27
 */
@Data
public class StudentFact implements Fact{

    private String name;

    private int grade;

    /**
     * M/F
     */
    private String gender;

    private int age;

    /**
     * 身高cm
     */
    private int height;

}
