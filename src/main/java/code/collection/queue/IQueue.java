package code.collection.queue;

/**
 * 〈队列〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public interface IQueue<E> {

    int size();

    boolean enqueue(E e);

    E dequeue();

}
