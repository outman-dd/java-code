package code.java8.functional;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
public class ConverterTest {

    public static void main(String[] args) {
        Converter<String, Integer> converter = from -> Integer.valueOf(from);
        Integer converted = converter.convert("123");
        System.out.println(converted);    // 123
    }
}
