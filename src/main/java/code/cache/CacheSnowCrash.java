package code.cache;

import code.cache.db.IDb;
import code.cache.db.SimpleDb;
import code.distribution.lock.DistributedLock;
import code.distribution.lock.ZooKeeperLock;

import java.util.Random;

/**
 * 〈缓存雪崩〉<p>
 * 缓存雪崩是指在我们设置缓存时采用了相同的过期时间，导致缓存在某一时刻同时失效，请求全部转发到DB，DB瞬时压力过重雪崩。
 *
 * 解决方案
 * 1、在缓存失效后，通过加锁或者队列来控制读数据库写缓存的线程数量。比如对某个key只允许一个线程查询数据和写缓存，其他线程等待。
 * 2、不同的key，设置不同的过期时间，让缓存失效的时间点尽量均匀
 *
 * @author zixiao
 * @date 18/6/13
 */
public class CacheSnowCrash {

    private ICache<String, Object> cache = new SimpleCache();

    private IDb<String, String> db = new SimpleDb<String, String>();

    public static int EXPIRED_SEC = 10;

    private static String MUTEX_KEY = "mutex_";

    private DistributedLock lock = new ZooKeeperLock();

    /**
     * 互斥锁 Mutex
     * @param key
     * @return
     * @throws InterruptedException
     */
    public Object getMutexKey(String key) throws InterruptedException {
        Object value = cache.get(key);
        if (value == null) {
            String keyMutex = MUTEX_KEY + key;
            if (cache.setnx(keyMutex, null)) {
                try {
                    cache.expire(keyMutex, 60);
                    return getFromDb(key);
                } finally {
                    cache.delete(keyMutex);
                }
            } else { //其他线程在设置缓存
                Thread.sleep(50);
                return getMutexKey(key);
            }
        } else {
            return value;
        }
    }

    /**
     * 锁
     * 确保只有一个线程
     * @param key
     * @return
     * @throws InterruptedException
     */
    public Object getWithLock(String key) throws InterruptedException {
        Object value = cache.get(key);
        if (value == null) { //代表缓存值过期
            String lockKey = key;
            lock.lock(lockKey);
            try {
                //双重判断
                if((value = cache.get(key)) == null){
                    return getFromDb(key);
                }else{
                    return value;
                }
            } finally {
                lock.unlock(lockKey);
            }
        } else{
            return value;
        }
    }

    private Object getFromDb(String key){
        Object value = db.get(key);
        setCache(key, value, false);
        return value;
    }

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @param randomExpired 随机失效时间
     */
    private void setCache(String key, Object value, boolean randomExpired){
        if(randomExpired){
            int halfExpiredSec = EXPIRED_SEC /2;
            int randomExpiredSec = new Random().nextInt(halfExpiredSec) + halfExpiredSec;
            cache.set(key, value, randomExpiredSec);
        }else{
            cache.set(key, value, EXPIRED_SEC);
        }

    }

    public void put(String key, String value) {
        db.put(key, value);
        setCache(key, value, false);
    }

    /**
     * 设置不同的过期时间，让缓存失效的时间点尽量均匀
     * @param key
     * @param value
     */
    public void putWithRandomExpired(String key, String value) {
        db.put(key, value);
        setCache(key, value, true);
    }

    public void stop(){
        cache.stop();
    }

}
