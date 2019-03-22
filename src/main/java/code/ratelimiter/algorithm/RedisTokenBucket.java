package code.ratelimiter.algorithm;

import code.cache.redis.Redis;
import code.cache.redis.SimpleRedis;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈Redis令牌桶〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/21
 */
public class RedisTokenBucket{

    private Redis redis = new SimpleRedis();

    /**
     * 获取令牌
     --- 返回码
     --- 0 没有令牌桶配置
     --- -1 表示取令牌失败，也就是桶里没有令牌
     --- 1 表示取令牌成功
     * @param key       令牌的唯一标识
     * @param permits   请求令牌数量
     * @param currMillSecond    当前毫秒数
     * @param context   使用令牌的应用标识
     * @return
     */
    public int acquire(String key, int permits, long currMillSecond, String context){
        return redisCallByLua(key, permits, currMillSecond, context);
    }

    /**
     * 模拟lua脚本调用redis
     * 原子操作
     * @param key
     * @param permits
     * @param currMillSecond
     * @param context
     * @return
     */
    private int redisCallByLua(String key, int permits, long currMillSecond, String context){
        Map<String, Object> rateLimitInfo = redis.hmget(key, "last_mill_second", "curr_permits", "max_permits", "rate", "apps");
        if(rateLimitInfo == null || rateLimitInfo.isEmpty()){
            return 0;
        }
        Long lastMillSecond = (Long)rateLimitInfo.get("last_mill_second");
        int currPermits = (Integer)rateLimitInfo.get("curr_permits");
        int maxPermits = (Integer)rateLimitInfo.get("max_permits");
        int rate = (Integer)rateLimitInfo.get("rate");
        String apps = (String)rateLimitInfo.get("apps");

        //没有配置令牌桶
        if(apps == null || apps.length() == 0 || !apps.contains(context)){
            return 0;
        }

        int localCurrPermits = maxPermits;

        //令牌桶刚刚创建，【上一次获取令牌的毫秒数】为空
        if(lastMillSecond != null){
            //根据和上一次向桶里添加令牌的时间和当前时间差，计算【应增加令牌数】
            int shouldAddPermits = Double.valueOf(Math.floor((currMillSecond - lastMillSecond) / 1000 * rate)).intValue();
            int expectCurrPermits = shouldAddPermits + currPermits;
            //计算【当前应有令牌数】
            localCurrPermits = Math.min(expectCurrPermits, maxPermits);

            // 大于0 表示需要增加令牌数，更新【上一次向桶里添加令牌的时间】; 否则本次不需要更新
            if(shouldAddPermits > 0){
                redis.hset(key, "last_mill_second", currMillSecond);
            }
        //更新上一次向桶里添加令牌的时间
        }else{
            redis.hset(key, "last_mill_second", currMillSecond);
        }

        int result = -1;
        //【当前应有令牌数】> 需要的令牌数，则更新当前剩余令牌数
        if(localCurrPermits >= permits){
            redis.hset(key, "curr_permits", localCurrPermits - permits);
            result = 1;
        }else{
            redis.hset(key, "curr_permits", localCurrPermits);
        }
        return result;
    }

    /**
     * --- 初始化令牌桶配置
     * @param key 令牌的唯一标识
     * @param maxPermits 桶大小
     * @param rate  向桶里添加令牌的速率
     * @param apps  可以使用令牌桶的应用列表，应用之前用逗号分隔
     */
    public void init(String key, int maxPermits, int rate, String apps){
        Map<String, Object> rateLimitInfo = redis.hmget(key, "last_mill_second", "curr_permits", "max_permits", "rate", "apps");
        if(rateLimitInfo == null){
            rateLimitInfo = new HashMap<>();
            rateLimitInfo.put("last_mill_second", null);
            rateLimitInfo.put("curr_permits", 0);
            rateLimitInfo.put("max_permits", maxPermits);
            rateLimitInfo.put("rate", rate);
            rateLimitInfo.put("apps", apps);
            redis.hmsetNx(key, rateLimitInfo);
        }else{
            int originCurrPermits = (Integer)rateLimitInfo.get("curr_permits");
            int originMaxPermits = (Integer)rateLimitInfo.get("max_permits");
            int originRate = (Integer)rateLimitInfo.get("rate");
            String originApps = (String)rateLimitInfo.get("apps");
            if(maxPermits != originMaxPermits || rate != originRate || !apps.equals(originApps)){
                Redis.Pair<String, Integer> currPermitsPair = new Redis.Pair<>("curr_permits", Math.min(originCurrPermits, maxPermits));
                Redis.Pair<String, Integer> maxPermitsPair = new Redis.Pair<>("max_permits", maxPermits);
                Redis.Pair<String, Integer> ratePair = new Redis.Pair<>("rate", rate);
                Redis.Pair<String, String> appsPair = new Redis.Pair<>("apps", apps);
                redis.hmset(key, currPermitsPair, maxPermitsPair, ratePair, appsPair);
            }
        }
    }


}
