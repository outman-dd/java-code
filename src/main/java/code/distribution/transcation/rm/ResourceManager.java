package code.distribution.transcation.rm;


import code.distribution.transcation.common.ArgContext;

import java.util.concurrent.Callable;

/**
 * 〈RM〉<p>
 * Resource Manager(RM):
 * Manage resources that branch transactions working on,
 * talk to TC for registering branch transactions and reporting status of branch transactions,
 * and drive the branch transaction commit or rollback.
 *
 * @author zixiao
 * @date 18/7/13
 */
public interface ResourceManager {

    Object doLocalTransaction(String xid, Callable callable, ArgContext argContext) throws Exception;

    boolean commitToTc(String xid, String resourceId);

    boolean rollbackToTc(String xid, String resourceId);
}
