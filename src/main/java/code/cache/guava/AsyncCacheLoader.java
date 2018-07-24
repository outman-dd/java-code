package code.cache.guava;

import com.google.common.cache.CacheLoader;

import java.util.concurrent.Executor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 〈支持异步刷新的CacheLoader〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/7/24
 */
public class AsyncCacheLoader{

    public static <K, V> CacheLoader<K, V> build(final DbLoader<K, V> dbLoader, final Executor executor){
        checkNotNull(dbLoader);
        checkNotNull(executor);
        return CacheLoader.asyncReloading(new CacheLoader<K, V>() {
            @Override
            public V load(K o) throws Exception {
                return dbLoader.get(o);
            }
        }, executor);
    }

}
