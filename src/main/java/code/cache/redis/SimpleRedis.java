package code.cache.redis;

import code.concurrency.lock.ILock;
import code.concurrency.lock.ReentrantLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈SimpleRedis〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/21
 */
public class SimpleRedis implements Redis{

    private Map<String, HashMap<String, Object>> storage = new ConcurrentHashMap<>(256);

    private ILock lock = new ReentrantLock();

    @Override
    public Object hget(String key, String field) {
        Map<String, Object> map = storage.get(key);
        if(map != null){
            return map.get(field);
        }
        return null;
    }

    @Override
    public boolean hset(String key, String field, Object value) {
        lock.lock();
        try {
            Map<String, Object> map = storage.get(key);
            if(map == null){
                storage.put(key, new HashMap<>());
                map = storage.get(key);
            }
            map.put(field, value);
            return true;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public Map<String, Object> hmget(String key, String... fields) {
        return storage.get(key);
    }

    @Override
    public boolean hmset(String key, Pair<String, ?>... fieldValues) {
        lock.lock();
        try {
            Map<String, Object> map = storage.get(key);
            if(map == null){
                storage.put(key, new HashMap<>());
                map = storage.get(key);
            }
            for (Pair<String, ?> pair : fieldValues) {
                map.put(pair.key, pair.value);
            }
            return true;
        }finally {
            lock.unlock();
        }
    }

    @Override
    public Map<String, Object> hmsetNx(String key, Map<String, Object> map) {
        return storage.putIfAbsent(key, (HashMap<String, Object>)map);
    }

    public ILock getLockObj(){
        return lock;
    }

}
