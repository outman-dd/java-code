package code.java8.references;

import code.java8.functional.Converter;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
public class Something {

    public String startsWith(String s) {
        return String.valueOf(s.charAt(0));
    }

    public static void main(String[] args) {
        Something something = new Something();
        Converter<String, String> converter = something::startsWith;
        System.out.println(converter.convert("java"));
    }
}
