package code.distribution.transcation.tc;

import code.distribution.transcation.common.BranchStatus;
import code.distribution.transcation.common.LockKey;
import code.distribution.transcation.rm.ResourceManager;
import lombok.Data;

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

    private String bizType;

    private String resourceId;

    private LockKey lockKey;

    public BranchSession(ResourceManager resourceManager, String xid, String resourceId, LockKey lockKey) {
        this.resourceManager = resourceManager;
        this.xid = xid;
        this.resourceId = resourceId;
        this.lockKey = lockKey;
    }

    public boolean lock(){
        return LockManager.lock(this);
    }

    public void unlock(){
        LockManager.unlock(this);
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
