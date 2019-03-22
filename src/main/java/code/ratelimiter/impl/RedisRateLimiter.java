package code.ratelimiter.impl;

import code.ratelimiter.RateLimiter;
import code.ratelimiter.algorithm.RedisTokenBucket;

/**
 * 〈Redis限速器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/21
 */
public class RedisRateLimiter implements RateLimiter {

    private static String key = "redisLimiter";

    private static String app = "test";

    private static RedisTokenBucket redisTokenBucket = new RedisTokenBucket();
    static {
        redisTokenBucket.init(key, 100, 10, app);
    }

    @Override
    public boolean canPass(int acquireCount) {
        return redisTokenBucket.acquire(key, acquireCount, System.currentTimeMillis(), app) == 1;
    }

}
