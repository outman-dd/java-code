package code.distribution.transcation.tc;

import code.distribution.transcation.common.BranchStatus;
import code.distribution.transcation.common.LockKey;
import code.distribution.transcation.rm.ResourceManager;

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
