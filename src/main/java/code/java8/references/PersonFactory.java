package code.java8.references;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/2
 */
interface PersonFactory<P extends Person> {
    P create(String firstName, String lastName);
}