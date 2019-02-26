package code.distribution.transcation.tc;

import code.distribution.transcation.common.LockKey;
import code.distribution.transcation.common.RoleType;
import code.distribution.transcation.utils.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class LockManager {

    private static Log log = Log.getLog(RoleType.TC);

    private static ConcurrentHashMap<String/*bizType*/, ConcurrentHashMap<LockKey, String>> lockMap = new ConcurrentHashMap<>();

    public static boolean lock(BranchSession branchSession) {
        String bizType = branchSession.getBizType();
        LockKey lockKey = branchSession.getLockKey();
        if(!lockMap.contains(bizType)){
            lockMap.putIfAbsent(bizType, new ConcurrentHashMap<LockKey, String>());
        }
        Map<LockKey, String> dbLock = lockMap.get(bizType);

        String lockXid = dbLock.get(branchSession.getLockKey());
        //不存在锁，则放入（考虑并发）
        if(lockXid == null){
            dbLock.putIfAbsent(lockKey, branchSession.getXid());
            lockXid = dbLock.get(lockKey);
        }
        //重新判断是否当前xid持有该锁
        if(branchSession.getXid().equals(lockXid)){
            log.info("BranchSession get global lock success. {0}, xid={1}", lockKey, branchSession.getXid());
            return true;
        }else{
            log.info("BranchSession get global lock fail, is hold by xid={0}, {1} ", lockXid, branchSession.getLockKey());
            return false;
        }
    }

    public static void unlock(BranchSession branchSession) {
        Map<LockKey, String> dbLock = lockMap.get(branchSession.getResourceId());
        if(dbLock != null){
            dbLock.remove(branchSession.getLockKey());
        }
    }
}
