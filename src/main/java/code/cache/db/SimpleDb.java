package code.cache.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        this.dataMap = new ConcurrentHashMap<>(1024);
    }

    public V get(K key) {
        sleep(5);
        //System.out.println("[DEBUG]Db query，key："+key);
        return dataMap.get(key);
    }

    public V put(K key, V value) {
        sleep(10);
        //System.out.println("[DEBUG]Db insert，key：" + key);
        return dataMap.put(key, value);
    }

    public V remove(K key) {
        sleep(10);
        //System.out.println("[DEBUG]Db delete，key："+key);
        return dataMap.remove(key);
    }

    @Override
    public void close() {
        dataMap.clear();
    }

    private void sleep(long mills){
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Map<K, V> getAll(){
        return this.dataMap;
    }
}
