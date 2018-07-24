package code.cache.guava;

/**
 * 〈数据库加载器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/7/24
 */
public interface DbLoader<K, V> {

    V get(K key);
}
