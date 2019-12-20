package code.collection.list;

/**
 * 〈列表〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/22
 */
public interface IList<E> {

    int size();

    /**
     * 查找元素第一次出现的位置
     * @param e
     * @return 元素第一次出现的位置；不存在返回-1
     */
    int indexOf(E e);

    /**
     * 随机查找
     * @param index
     * @return
     */
    E get(int index);

    /**
     * 加入尾部
     * @param e
     */
    void add(E e);

    /**
     * 在指定位置加入
     * @param index
     * @param e
     */
    void add(int index, E e);

    /**
     * 删除
     * @param e
     * @return
     */
    boolean remove(E e);

    /**
     * 删除指定位置
     * @param index
     */
    E remove(int index);
}
