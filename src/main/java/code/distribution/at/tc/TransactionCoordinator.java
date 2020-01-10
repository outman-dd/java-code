package code.distribution.at.tc;

import code.distribution.at.common.BranchStatus;
import code.distribution.at.common.LockKey;
import code.distribution.at.rm.ResourceManager;

/**
 * 〈TC〉<p>
 * Transaction Coordinator(TC):
 * Maintain status of global and branch transactions, drive the global commit or rollback.
 *
 * @author zixiao
 * @date 2019/2/25
 */
public interface TransactionCoordinator {

    String beginGlobal();

    boolean commitGlobal(String xid);

    boolean rollbackGlobal(String xid);

    boolean registerBranch(String xid, String resourceId, LockKey lockKey, ResourceManager rm);

    boolean reportBranch(String xid, String resourceId, BranchStatus branchStatus);
}
