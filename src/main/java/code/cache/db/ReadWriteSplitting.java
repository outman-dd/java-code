package code.cache.db;

import code.cache.ICache;
import code.cache.SimpleCache;

/**
 * 〈读写分离 如何保证主从一致〉<p>
 * 1、写数据 先写主库，同时缓存key，设置失效时间 ~= 主从同步延迟
 * 2、读数据 先从缓存中找key，存在走主库，否则读从库
 *
 * @author zixiao
 * @date 2019/10/11
 */
public class ReadWriteSplitting<K, V> {

    private ICache<K, V> cache = new SimpleCache<>();

    //主从延迟时间 900ms
    private IDb<K, V> db = new MasterSlaveDb<>(900);

    /**
     * 写数据 先写主库，同时缓存key，设置失效时间 ~= 主从同步延迟
     * @param key
     * @param value
     */
    public void write(K key, V value){
        db.put(key, value);
        //失效时间略大于主从延迟时间
        cache.set(key, null, 1);
    }

    /**
     * 读数据 先从缓存中找key，存在走主库，否则读从库
     * @param key
     * @return
     */
    public V read(K key){
        if(cache.exist(key)){
            System.out.print(">> read master:");
            return ((MasterSlaveDb<K, V>)db).getFromMaster(key);
        }
        System.out.print(">> read slave:");
        return db.get(key);
    }

    public void close(){
        cache.stop();
        db.close();
    }

    public static void main(String[] args) throws InterruptedException {
        ReadWriteSplitting rwSplitting = new ReadWriteSplitting();
        String key = "test";
        rwSplitting.write(key, System.currentTimeMillis());

        for (int i = 0; i < 5; i++) {
            Thread.sleep(300);
            System.out.println(String.format("%s=>%s", key, rwSplitting.read(key)));
        }

        System.out.println("-----------------");

        rwSplitting.write(key, System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(300);
            System.out.println(String.format("%s=>%s", key, rwSplitting.read(key)));
        }

        rwSplitting.close();
    }

}
