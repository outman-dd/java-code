package code.java8.predicates;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
public class PredicateTest {

    public static void main(String[] args) {
        Predicate<String> predicate = (s) -> s.length() > 0;

        System.out.println(predicate.test("foo"));              // true
        System.out.println(predicate.negate().test("foo"));     // false

        Predicate < Boolean > nonNull = Objects::nonNull;
        System.out.println(nonNull.test(null));

        Predicate<Boolean> isNull = Objects::isNull;
        System.out.println(isNull.test(null));

        Predicate<String> isEmpty = String::isEmpty;
        System.out.println(isEmpty.test(""));

        Predicate<String> isNotEmpty = isEmpty.negate();
        System.out.println(isNotEmpty.test(""));

    }
}
