package code.distribution.tcc.rm;

import code.distribution.tcc.common.BranchState;
import code.distribution.tcc.common.TxFlowDo;
import code.distribution.tcc.common.TxMethod;
import code.distribution.tcc.common.TxState;
import code.distribution.tcc.exception.TccException;
import code.distribution.tcc.tc.TccTxCoordinator;
import code.distribution.tcc.tc.TxCoordinator;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/9
 */
public class TccResManager implements ResManager{

    private TxCoordinator tc = TccTxCoordinator.getInstance();

    private static ResManager instance = new TccResManager();

    public static ResManager getInstance() {
        return instance;
    }

    @Override
    public boolean registerBranch(String xid, String branchId, TxMethod tccMethod) {
        return tc.registerBranch(xid, branchId, tccMethod);
    }

    @Override
    public boolean onePhase(String xid, String branchId) {
        return tc.reportBranch(xid, branchId, BranchState.ONE_PHASE_OK);
    }

    @Override
    public boolean commit(String xid, String branchId) {
        return tc.reportBranch(xid, branchId, BranchState.TWO_COMMIT_OK);
    }

    @Override
    public boolean rollback(String xid, String branchId) {
        return tc.reportBranch(xid, branchId, BranchState.TWO_ROLLBACK_OK);
    }

    /**
     * 防悬挂，防止cancel执行后，有执行try 导致预留资源无法释放
     * 先插入事务控制表记录，
     * 如果插入成功，说明第二阶段还没有执行，可以继续执行第一阶段。
     * 如果插入失败，则说明第二阶段已经执行或正在执行，则抛出异常，终止即可。
     *
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public boolean beforeTry(String xid, String branchId) {
        boolean insertSuccess = SimpleDb.insert(xid, branchId, TxState.INIT);
        if(!insertSuccess){
            throw new TccException("Two phase executed or executing");
        }
        return true;
    }

    /**
     * 先锁定事务记录，
     * 如果事务记录为空，则说明是一个空提交
     */
    @Override
    public boolean beforeConfirm(String xid, String branchId) {
        TxFlowDo txFlowDo = SimpleDb.selectForUpdate(xid, branchId);

        //如果事务记录为空，则说明是一个空提交，不允许，终止执行
        if(txFlowDo == null){
            throw new TccException("Blank commit, one phase not executed.");
        }

        //幂等控制
        if(TxState.COMMIT == txFlowDo.getState()){
            //已提交，直接返回成功
            return false;
        }else if(TxState.ROLLBACK == txFlowDo.getState()){
            //已回滚，异常报警
            throw new TccException("Two rolled back, can not commit.");
        }
        //状态为初始化，说明一阶段正确执行，可以执行二阶段
        return true;
    }

    /**
     * 先锁定事务记录，
     * 如果事务记录为空，则说明是一个空回滚
     */
    @Override
    public boolean beforeCancel(String xid, String branchId) {
        TxFlowDo txFlowDo = SimpleDb.selectForUpdate(xid, branchId);

        //如果事务记录为空，则说明是一个空回滚
        if(txFlowDo == null){
            //先插入一条事务记录，确保后续的 Try 方法不会再执行
            boolean insertSuccess = SimpleDb.insert(xid, branchId, TxState.ROLLBACK);
            if (insertSuccess) {
                //如果插入成功，则说明 Try 方法还没有执行，空回滚继续执行
                return false;
            }else{
                //try正在执行，回滚失败 等待下次重试
                throw new TccException("Roll back fail.");
            }
        }

        //幂等控制
        if(TxState.ROLLBACK == txFlowDo.getState()){
            //已回滚，直接返回成功
            return false;
        }else if(TxState.COMMIT == txFlowDo.getState()){
            //已提交，异常报警
            throw new TccException("Two committed, can not roll back.");
        }
        //状态为初始化，说明一阶段正确执行，可以执行二阶段
        return true;
    }

}
