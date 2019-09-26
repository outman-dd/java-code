package code.distribution.lock;

/**
 * 〈分布式锁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/3/20
 */
public interface DistributedLock {

    /**
     * 获取锁
     * @param key   业务Key
     * @return
     */
    void lock(String key);

    /**
     * 解锁
     * @param key     业务Key
     * @return
     */
    void unlock(String key);
}
