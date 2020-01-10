package code.distribution.at.tc;

import code.distribution.at.common.BranchStatus;
import code.distribution.at.common.LockKey;
import code.distribution.at.rm.ResourceManager;
import lombok.Data;

import java.util.Iterator;
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
@Data
public class BranchSession {

    private BranchStatus branchStatus;

    private ResourceManager resourceManager;

    private String xid;

    private String resourceId;

    private LockKey lockKey;

    private ConcurrentHashMap<Map<String, String>, Set<String/*pk*/>> lockHolder = new ConcurrentHashMap();

    public BranchSession(ResourceManager resourceManager, String xid, String resourceId, LockKey lockKey) {
        this.resourceManager = resourceManager;
        this.xid = xid;
        this.resourceId = resourceId;
        this.lockKey = lockKey;
    }

    public boolean lock(){
        return LockManager.lock(this);
    }

    public boolean unlock(){
        if(lockHolder.size() == 0){
            return true;
        }

        Iterator<Map.Entry<Map<String, String>, Set<String>>> it = lockHolder.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Map<String, String>, Set<String>> entry = it.next();
            Map<String, String> item = entry.getKey();
            Set<String> pkValues = entry.getValue();
            synchronized (item) {
                for (String pk : pkValues) {
                    String lockingXid = item.get(pk);
                    if (xid.equals(lockingXid)) {
                        item.remove(pk);
                    }
                }
            }
        }
        lockHolder.clear();
        return true;
    }

    @Override
    public String toString() {
        return "BranchSession{" +
                "xid='" + xid + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", lockKey=" + lockKey +
                '}';
    }
}
