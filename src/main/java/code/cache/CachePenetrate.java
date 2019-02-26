package code.cache;

import java.util.concurrent.ExecutionException;

/**
 * 〈缓存穿透〉<p>
 * 访问一个不存在的key，缓存不起作用，请求会穿透到DB，流量大时DB会挂掉。
 *
 * 解决方案
 * 1、采用布隆过滤器，使用一个足够大的bitmap，用于存储可能访问的key，不存在的key直接被过滤；
   2、访问key未在DB查询到值，也将空值写进缓存，但可以设置较短过期时间。
 *
 * @author zixiao
 * @date 18/6/13
 */
public class CachePenetrate{

    private ICache<String, Object> cache = new SimpleCache();

    private IDb<String, Object> db = new SimpleDb();

    private int expiredSec = 10 * 60;

    private int shortSec = 5;

    private BloomFilter<String> bloomFilter = new BloomFilter<String>(1000000000, 0.01);

    /**
     * 访问key未在DB查询到值，也将空值写进缓存，但可以设置较短过期时间。
     *
     * @param key
     * @return
     * @throws ExecutionException
     */
    public Object get(String key) throws ExecutionException {
        Object value = cache.get(key);
        if(value != null){
            return value;
        }

        //value == null
        if(cache.exist(key)){
            return null;
        }else{
            value = db.get(key);
            if(value == null){
                cache.set(key, null, shortSec);
            }else{
                cache.set(key, value, expiredSec);
            }
            return value;
        }
    }

    /**
     * 采用布隆过滤器
     *
     * 1、布隆过滤器不存在，肯定不存在
     * 2、布隆过滤器存在，可能不存在
     * @param key
     */
    public Object getWithBloomFilter(String key){
        Object value = cache.get(key);
        if(value != null){
            return value;
        }

        if(bloomFilter.exist(key)){
            value = db.get(key);
            if(value == null){
                bloomFilter.remove(key);
            }else{
                cache.set(key, value, expiredSec);
            }
            return value;
        }else{
            return null;
        }
    }

    public void put(String key, String value) {
        db.put(key, value);
        cache.set(key, value, expiredSec);
        bloomFilter.add(key);
    }

    public void remove(String key) {
        db.remove(key);
        cache.delete(key);
        bloomFilter.remove(key);
    }

    public void stop(){
        cache.stop();
    }

}
