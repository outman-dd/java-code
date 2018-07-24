package code.cache;

import code.concurrency.util.ConcurrentExecutor;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/14
 */
public class CacheTest {

    private final String[] TEST_KEYS = {"key1", "key2","key3", "key4"};

    private int testNum = 100;

    @Test
    public void penetrateSetNull() throws ExecutionException, InterruptedException {
        String notExistKey = "key123";
        CachePenetrate cachePenetrate = new CachePenetrate();
        for(int i=0; i< 10; i++){
            System.out.println("*********第"+i+"次**********");
            cachePenetrate.get(notExistKey);
            Thread.sleep(800);
        }
        cachePenetrate.stop();
    }

    @Test
    public void penetrateBloomFilter() throws InterruptedException {
        String notExistKey = "key123";
        CachePenetrate cachePenetrate = new CachePenetrate();
        for(String key : TEST_KEYS){
            cachePenetrate.put(key, "value_"+key);
        }
        //TODO BloomFilter未实现
        for(int i=0; i< 10; i++){
            System.out.println("*********第"+i+"次**********");
            cachePenetrate.getWithBloomFilter(notExistKey);
            Thread.sleep(800);
        }
        cachePenetrate.stop();
    }


    @Test
    public void snowCrashMutex() throws InterruptedException {
        final CacheSnowCrash cacheTest = createSnowCrash();
        Thread.sleep(CacheSnowCrash.EXPIRED_SEC * 1000 + 50);

        ConcurrentExecutor executor = new ConcurrentExecutor(testNum);
        for(int i=0; i< testNum; i++){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + ", value:"+ cacheTest.getMutexKey("key1") );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Thread.sleep(10 * 1000);
        executor.shutdown();
        cacheTest.stop();
    }

    @Test
    public void snowCrashWithLock() throws InterruptedException {
        final CacheSnowCrash cacheTest = createSnowCrash();
        Thread.sleep(CacheSnowCrash.EXPIRED_SEC * 1000 + 50);

        ConcurrentExecutor executor = new ConcurrentExecutor(testNum);
        for(int i=0; i< testNum; i++){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(Thread.currentThread().getName() + ", value:"+ cacheTest.getWithLock("key1") );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        Thread.sleep(10 * 1000);
        executor.shutdown();
        cacheTest.stop();
    }

    @Test
    public void snowCrashRandomExpired(){
        CacheSnowCrash cacheSnowCrash = new CacheSnowCrash();
        for(String key : TEST_KEYS){
            cacheSnowCrash.putWithRandomExpired(key, "value_"+key);
        }
    }

    private CacheSnowCrash createSnowCrash(){
        CacheSnowCrash cacheSnowCrash = new CacheSnowCrash();
        for(String key : TEST_KEYS){
            cacheSnowCrash.put(key, "value_"+key);
        }
        return cacheSnowCrash;
    }
}
