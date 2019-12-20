package code.cache;

import code.cache.db.ReadWriteSplitting;

import java.util.Optional;

/**
 * 〈数据库，缓存读写顺序〉<p>
 ﻿写操作的顺序是：
 （1）删除cache；
 （2）写数据库；

 读操作的顺序是：
 （1）读cache，如果cache hit则返回；
 （2）如果cache miss，则读从库；
 （3）读从库后，将数据放回cache；
 *
 * @author zixiao
 * @date 2019/10/11
 */
public class WriteDbReadCache<K, V> {

    private ReadWriteSplitting<K, V> db = new ReadWriteSplitting<>();

    private ICache<K, Optional<V>> cache = new SimpleCache<>();

    /**
     * ﻿写操作的顺序是：
     （1）删除cache；
     （2）写数据库；
     * @param key
     * @param value
     */
    public void write(K key, V value){
        cache.delete(key);
        db.write(key, value);
    }

    /**
     *  读操作的顺序是：
     （1）读cache，如果cache hit则返回；
     （2）如果cache miss，则读从库；
     （3）读从库后，将数据放回cache；
     * @param key
     * @return
     */
    public V read(K key){
        Optional<V> ov = cache.get(key);
        if(ov != null){
            System.out.printf(">> read cache:");
            return ov.get();
        }else{
            V v = db.read(key);
            cache.set(key, Optional.ofNullable(v));
            return v;
        }
    }

    public void close(){
        cache.stop();
        db.close();
    }

    public static void main(String[] args) throws InterruptedException {
        WriteDbReadCache writeDbReadCache = new WriteDbReadCache();
        String key = "test";
        writeDbReadCache.write(key, System.currentTimeMillis());

        Thread.sleep(100);
        System.out.println(String.format("%s=>%s", key, writeDbReadCache.read(key)));

        System.out.println("-----------------");

        writeDbReadCache.write(key, System.currentTimeMillis());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(300);
            System.out.println(String.format("%s=>%s", key, writeDbReadCache.read(key)));
        }

        writeDbReadCache.close();
    }
}
