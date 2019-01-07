package code.java8.lambda;

import java.util.Arrays;
import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
public class LambdaTest {

    public static void main(String[] args) {
        List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

        names.sort((a,b)-> a.compareTo(b));
        names.forEach(item -> System.out.println(item));
    }
}
