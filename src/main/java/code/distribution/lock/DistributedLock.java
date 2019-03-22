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
     * @return      锁序号
     */
    String getLock(String key);

    /**
     * 解锁
     * @param key     业务Key
     * @param lockSeq 锁序号
     * @return
     */
    boolean unlock(String key, String lockSeq);
}
