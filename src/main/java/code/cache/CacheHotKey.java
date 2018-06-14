package code.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈重建热点Key〉<p>
 * 对于一些设置了过期时间的key，如果这些key可能会在某些时间点被超高并发地访问，是一种非常“热点”的数据。
 * 在缓存失效的瞬间，有大量线程来重建缓存，造成后端负载加大，甚至可能会让应用崩溃。
 *
 * 解决方案
 * 1、互斥锁(Mutex)
 * 2、永不过期
 * @author zixiao
 * @date 18/6/13
 */
public class CacheHotKey {

    private ICache<String, Object> cache = new SimpleCache();

    private IDb<String, Object> db = new SimpleDb();

    private int expiredSec = 10 * 60;

    private String MUTEX_KEY = "mutex_";

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    /**
     * 1.使用互斥锁(mutex key)
     * @param key
     * @return
     */
    public Object getMutexKey(String key) throws InterruptedException {
        Object value = cache.get(key);
        if (value == null) { //代表缓存值过期
            String keyMutex = MUTEX_KEY + key;
            if (cache.setnx(keyMutex, null)) {
                try {
                    //设置60s的超时，防止del操作失败的时，下次缓存过期一直不能load db
                    cache.expire(keyMutex, 60);
                    value = db.get(key);
                    cache.set(key, value, expiredSec);
                    return value;
                } finally {
                    cache.delete(keyMutex);
                }
            } else {  //这个时候代表同时候的其他线程已经load db并回设到缓存了，这时候重试获取缓存值即可
                Thread.sleep(50);
                //重试
                return getMutexKey(key);
            }
        } else {
            return value;
        }
    }

    /**
     * 2、永远不过期
     * @param key
     * @return
     */
    public Object get(final String key) {
        CacheValue cv = (CacheValue)cache.get(key);
        Object value = cv.getValue();
        long timeout = cv.getTimeout();
        if (timeout <= System.currentTimeMillis()) {
            // 异步更新后台异常执行
            threadPool.execute(new Runnable() {
                public void run() {
                    String keyMutex = MUTEX_KEY + key;
                    if (cache.setnx(keyMutex, null)) {
                        try {
                            // 3 min timeout to avoid mutex holder crash
                            cache.expire(keyMutex, 3 * 60);
                            Object dbValue = db.get(key);
                            cache.set(key, new CacheValue(System.currentTimeMillis() + expiredSec*1000, dbValue));
                        } finally {
                            cache.delete(keyMutex);
                        }
                    }
                }
            });
        }
        return value;
    }

    static class CacheValue{

        private long timeout;

        private Object value;

        public CacheValue(long timeout, Object value) {
            this.timeout = timeout;
            this.value = value;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public void put(String key, String value) {
        db.put(key, value);
        cache.set(key, value, expiredSec);
    }

    public void stop(){
        cache.stop();
    }
}
