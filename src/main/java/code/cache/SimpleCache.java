package code.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈简单的cache实现〉<p>
 *
 * @author zixiao
 * @date 18/6/13
 */
public class SimpleCache<K, V> implements ICache<K, V> {

    private int size;

    private Map<K, V> dataMap;

    private Map<K, Long> timeoutMap;

    private ScheduledExecutorService scheduledExecutorService;

    private ReentrantLock lock = new ReentrantLock();

    public SimpleCache() {
        this(1024);
    }

    public SimpleCache(int size) {
        this.size = size;
        dataMap = new HashMap<K, V>(this.size);
        timeoutMap = new HashMap<K, Long>(this.size);
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        //定时清理过期的缓存
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                Iterator<Map.Entry<K,Long>> iterator = timeoutMap.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<K,Long> entry = iterator.next();
                    if(entry.getValue() <= System.currentTimeMillis()){
                        System.out.println("[DEBUG]Cache expired key："+entry.getKey());
                        iterator.remove();
                        dataMap.remove(entry.getKey());
                    }
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public V get(K key) {
        return dataMap.get(key);
    }

    public boolean exist(K key) {
        return dataMap.containsKey(key);
    }

    public V set(K key, V value) {
        lock.lock();
        try{
            dataMap.put(key, value);
            return value;
        } finally {
            lock.unlock();
        }
    }

    public V set(K key, V value, int expireSeconds) {
        lock.lock();
        try {
            dataMap.put(key, value);
            timeoutMap.put(key, System.currentTimeMillis() + expireSeconds * 1000);
            return value;
        } finally {
            lock.unlock();
        }
    }

    public boolean setnx(K key, V value) {
        lock.lock();
        try {
            if(dataMap.containsKey(key)){
                return false;
            }
            dataMap.put(key, value);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void expire(K key, int expireSeconds) {
        lock.lock();
        try {
            timeoutMap.put(key, System.currentTimeMillis() + expireSeconds * 1000);
        } finally {
            lock.unlock();
        }
    }

    public V delete(K key) {
        lock.lock();
        try {
            timeoutMap.remove(key);
            return dataMap.remove(key);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }
}
