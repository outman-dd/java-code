package code.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈简单的Db实现〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/13
 */
public class SimpleDb<K, V> implements IDb<K, V> {

    private Map<K, V> dataMap;

    public SimpleDb() {
        this.dataMap = new HashMap<K, V>(1024);
    }

    public V get(K key) {
        System.out.println("数据库查询，key："+key);
        return dataMap.get(key);
    }

    public V put(K key, V value) {
        System.out.println("数据库插入，key："+key);
        return dataMap.put(key, value);
    }

    public V remove(K key) {
        System.out.println("数据库删除，key："+key);
        return dataMap.remove(key);
    }
}
