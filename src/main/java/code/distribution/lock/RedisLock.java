package code.distribution.lock;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈Redis锁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/8/26
 */
public class RedisLock implements DistributedLock {

    private RedisClient redisClient = new RedisClient();

    private final UUID id;

    public RedisLock() {
        this.id = UUID.randomUUID();
    }

    public RedisLock(UUID id) {
        this.id = id;
    }

    @Override
    public void lock(String key) {
        long expired = 3000;
        long threadId = Thread.currentThread().getId();
        Long ttl = tryAcquire(key, threadId, expired);
        if(ttl == null){
            return;
        }

        while (true){
            ttl = tryAcquire(key, threadId, expired);
            if(ttl == null){
                return;
            }
        }
    }

    private Long tryAcquire(String key, long threadId, long expired){
        return redisClient.tryLockInner(key, String.valueOf(threadId), expired);
    }

    @Override
    public void unlock(String key) {
        tryRelease(key, Thread.currentThread().getId());
    }

    private void tryRelease(String key, long threadId){
        redisClient.tryUnlockInner(key, String.valueOf(threadId));
    }

    class RedisClient{

        private Map<String/*lockKey*/, Map<String/*lockOwnerId*/, AtomicInteger>> lockHolder = new HashMap<>();

        private Map<String, ExpiredObject> expiredMap = new HashMap<>();

        public Long tryLockInner(String lockKey, String threadId, long expiredMills){
            String threadUk = id + threadId;
            //lua保证在 synchronized 块，一个事务里面
            synchronized (lockHolder){
                if(!exist(lockKey)){
                    hset(lockKey, threadUk, 1);
                    pexpire(lockKey, expiredMills);
                    System.out.println("Lock success, I'm the first guy.");
                    return null;
                } else if(hexist(lockKey, threadUk)){
                    int locks = hincrby(lockKey, threadUk, 1);
                    pexpire(lockKey, expiredMills);
                    System.out.println("Locked by me(reentrant), locks="+locks);
                    return null;
                }
                System.out.println("Locked by another guy.");
                return pttl(lockKey);
            }
        }

        public void tryUnlockInner(String lockKey, String threadId){
            String threadUk = UUID.randomUUID().toString() + threadId;
            if(!exist(lockKey)){
                System.out.println("no lock existed.");
                return;
            }
            else if(hexist(lockKey, threadUk)){
                int locks = hdecrby(lockKey, threadUk, 1);
                System.out.println("Unlock by me(reentrant), remain locks="+locks);
                return;
            }
        }

        public boolean exist(String key){
            return lockHolder.containsKey(key) ;
        }

        public void hset(String key, String subKey, int value){
            Map<String, AtomicInteger> map = new HashMap<>();
            map.put(subKey, new AtomicInteger(Integer.valueOf(value)));
            lockHolder.put(key, map);
        }

        /**
         * ms为单位
         */
        public void pexpire(String key, long expireMills){
            //expire
            expiredMap.put(key, new ExpiredObject(expireMills));
        }

        public boolean hexist(String key, String subKey){
            Map<String, AtomicInteger> map = lockHolder.get(key);
            if( map != null && map.containsKey(subKey)){
                return true;
            }
            return false;
        }

        public int hincrby(String key, String subKey, int delta){
            Map<String, AtomicInteger> map = lockHolder.get(key);
            return map.get(subKey).addAndGet(delta);
        }

        public int hdecrby(String key, String subKey, int delta){
            Map<String, AtomicInteger> map = lockHolder.get(key);
            return map.get(subKey).addAndGet(-1 * delta);
        }

        public Long pttl(String key){
            ExpiredObject expiredObject = expiredMap.get(key);
            long esapled = System.currentTimeMillis() - expiredObject.start;
            if(esapled >= expiredObject.getExpiredMills()){
                return null;
            }else{
                return expiredObject.getExpiredMills() - esapled;
            }
        }
    }


    @Data
    class ExpiredObject{

        private long start;

        private long expiredMills;

        public ExpiredObject(long expiredMills) {
            this.start = System.currentTimeMillis();
            this.expiredMills = expiredMills;
        }
    }
}
