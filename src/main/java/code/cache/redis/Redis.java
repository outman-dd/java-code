package code.cache.redis;

import java.util.Map;

/**
 * 〈Redis〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/21
 */
public interface Redis {

    Object hget(String key, String field);

    boolean hset(String key, String field, Object value);

    Map<String, Object> hmget(String key, String... fields);

    boolean hmset(String key, Pair<String, ?>... fieldValues);

    Map<String, Object> hmsetNx(String key, Map<String, Object> map);

    class Pair<K,V> {
        K key;
        V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}
