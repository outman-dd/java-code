package code.distribution.transcation.tc;

import code.distribution.transcation.common.LockKey;
import code.distribution.transcation.common.RoleType;
import code.distribution.transcation.utils.Log;
import io.netty.util.internal.ConcurrentSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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

    private static ConcurrentHashMap<String/*resourceId*/, ConcurrentHashMap<String/*tableName*/, Map<String/*pk*/, String/*xid*/>>> lockMap = new ConcurrentHashMap<>();

    public static boolean lock(BranchSession branchSession) {
        String resourceId = branchSession.getResourceId();
        if(!lockMap.contains(resourceId)){
            lockMap.putIfAbsent(resourceId, new ConcurrentHashMap<String, Map<String, String>>());
        }

        Map<String, Map<String, String>> dbLockMap = lockMap.get(resourceId);
        LockKey lockKey = branchSession.getLockKey();
        Map<String, String> tableLockMap = dbLockMap.get(lockKey.getTableName());
        if(tableLockMap == null){
            dbLockMap.put(lockKey.getTableName(), new HashMap<String, String>());
            tableLockMap = dbLockMap.get(lockKey.getTableName());
        }

        ConcurrentHashMap<Map<String, String>, Set<String>> lockHolder = branchSession.getLockHolder();
        synchronized (tableLockMap){
            String myXid = branchSession.getXid();
            for(String pk : lockKey.getPkValues()){
                String lockingXid = tableLockMap.get(pk);
                //不存在锁，则放入（考虑并发）
                if(lockingXid == null){
                    tableLockMap.put(pk, myXid);
                    Set<String> keysSet = lockHolder.get(tableLockMap);
                    if (keysSet == null) {
                        lockHolder.putIfAbsent(tableLockMap, new ConcurrentSet<String>());
                        keysSet = lockHolder.get(tableLockMap);
                    }
                    keysSet.add(pk);
                //重新判断是否当前xid持有该锁
                }else if(!lockingXid.equals(myXid)){
                    log.info("BranchSession get global lock fail, is hold by xid={0}, {1}:{2} ", lockingXid, lockKey.getTableName(), pk);
                    //释放占用lock(其他pk值)
                    branchSession.unlock();
                    return false;
                }else{
                    //lock by me
                    continue;
                }
            }
            log.info("BranchSession get global lock success. xid={0}, {1}", branchSession.getXid(), lockKey);
            return true;
        }

    }
}
