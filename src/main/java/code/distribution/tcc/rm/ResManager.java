package code.distribution.tcc.rm;

import code.distribution.tcc.common.TxMethod;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/9
 */
public interface ResManager {

    boolean registerBranch(String xid, String branchId, TxMethod tccMethod);

    boolean onePhase(String xid, String branchId);

    boolean commit(String xid, String branchId);

    boolean rollback(String xid, String branchId);

    boolean beforeTry(String xid, String branchId);

    boolean beforeConfirm(String xid, String branchId);

    boolean beforeCancel(String xid, String branchId);


}
