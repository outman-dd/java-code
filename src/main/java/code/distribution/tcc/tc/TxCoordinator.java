package code.distribution.tcc.tc;

import code.distribution.tcc.common.BranchState;
import code.distribution.tcc.common.TxMethod;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
public interface TxCoordinator {

    String beginGlobal();

    boolean registerBranch(String xid, String branchId, TxMethod tccMethod);

    boolean commitGlobal(String xid);

    boolean rollbackGlobal(String xid);

    boolean reportBranch(String xid, String branchId, BranchState state);

}
