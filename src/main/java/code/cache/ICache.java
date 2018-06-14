package code.cache;

/**
 * 〈缓存〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/13
 */
public interface ICache<K, V> {

    V get(K key);

    boolean exist(K key);

    V set(K key, V value);

    V set(K key, V value, int expireSeconds);

    boolean setnx(K key, V value);

    void expire(K key, int expireSeconds);

    V delete(K key);

    void stop();

}