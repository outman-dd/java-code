package code.java8.functions;

import java.util.function.Function;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
public class FunctionTest {
    public static void main(String[] args) {
        Function<String, Integer> toInteger = Integer::valueOf;

        Function<String, String> backToString = toInteger.andThen(integer -> integer+"_");

        backToString.apply("123");     // "123"
    }
}
