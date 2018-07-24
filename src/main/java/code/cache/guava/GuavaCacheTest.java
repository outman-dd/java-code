package code.cache.guava;

import code.util.DateFormatUtils;
import com.google.common.cache.*;

import java.util.Date;
import java.util.concurrent.*;

/**
 * 〈缓存测试〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/7/24
 */
public class GuavaCacheTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final DbLoader<String, String> dbLoader = new DbLoader<String, String>() {
            @Override
            public String get(String key) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "V"+System.currentTimeMillis();
            }
        };
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                try {
                    executor.getQueue().put(r);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
        });

        CacheLoader loader = AsyncCacheLoader.build(dbLoader, executor);

        LoadingCache<String, String> loadingCache = CacheBuilder.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .concurrencyLevel(4)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .refreshAfterWrite(4, TimeUnit.SECONDS)
                .removalListener(new RemovalListener<String, String>() {
                    @Override
                    public void onRemoval(RemovalNotification<String, String> notification) {
                        System.out.println(Thread.currentThread().getName() + ": key "+notification.getKey()+" removed at "+ DateFormatUtils.format(new Date(), "HH:mm:ss"));
                    }
                })
                .recordStats()
                .build(loader);

        for(int i=0; i<2000; i++){
            Thread.sleep(10L);
            loadingCache.get("1");
        }
    }
}
