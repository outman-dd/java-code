package code.java8.functional;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
@FunctionalInterface
public interface Converter<F, T> {
    T convert(F from);
}