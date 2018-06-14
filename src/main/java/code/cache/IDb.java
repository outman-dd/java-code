package code.cache;

/**
 * 〈DB〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/13
 */
public interface IDb<K, V> {

    V get(K key);

    V put(K key, V value);

    V remove(K key);

}
