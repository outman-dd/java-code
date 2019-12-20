package code.collection.stack;

/**
 * 〈栈〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public interface IStack<E> {

    int size();

    void push(E e);

    E pop();

    void clear();
}
