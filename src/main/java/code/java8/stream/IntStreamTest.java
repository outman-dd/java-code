package code.java8.stream;

import java.util.stream.IntStream;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
public class IntStreamTest {

    public static void main(String[] args) {
        IntStream.range(0, 10).forEach(i -> System.out.println(i));
    }
}
