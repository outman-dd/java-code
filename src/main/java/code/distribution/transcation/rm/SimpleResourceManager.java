package code.distribution.transcation.rm;


import code.distribution.transcation.common.*;
import code.distribution.transcation.exception.TxLockException;
import code.distribution.transcation.exception.TxLockTimeoutException;
import code.distribution.transcation.tc.DefaultTransactionCoordinator;
import code.distribution.transcation.tc.TransactionCoordinator;
import code.distribution.transcation.utils.Log;
import code.distribution.transcation.utils.ResourceId;

import java.util.concurrent.Callable;

/**
 * 〈资源管理器〉<p>
 * 〈管理分支事务所在的资源，与TC交互，注册分支事务，上报分支事务状态，驱动分支事务的提交和回滚〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class SimpleResourceManager implements ResourceManager {

    private Log log = Log.getLog(RoleType.RM);

    private long timeout = 10 * 1000;

    private TransactionCoordinator tc = DefaultTransactionCoordinator.getInstance();

    private SqlExecutor sqlExecutor;

    public SimpleResourceManager() {

    }

    @Override
    public Object doLocalTransaction(String xid, Callable callable, ArgContext argContext) throws Exception {
        return doLocalTransaction(xid, callable, argContext, System.currentTimeMillis());
    }

    /**
     * 一阶段，提交本地事务和保存undo日志
     * @param xid
     * @param callable
     * @param argContext
     * @param startTime
     * @return
     * @throws Exception
     */
    private Object doLocalTransaction(String xid, Callable callable, ArgContext argContext, long startTime) throws Exception {
        LockKey lockKey = new LockKey(argContext);
        String resourceId = ResourceId.newResourceId(argContext.getBizType());
        BranchStatus branchStatus = null;
        try {
            //注册分支事务，获取全局锁
            if(!register(xid, resourceId, lockKey, this)){
                throw new TxLockException(lockKey + " is locked.");
            }

            //记录日志和执行sql
            sqlExecutor = new SimpleSqlExecutor(xid, lockKey);
            UndoLog undoLog = new UndoLog(xid, sqlExecutor.beforeImage(), sqlExecutor.afterImage());
            Object ret  = doInTransaction(undoLog, callable, resourceId);

            branchStatus = BranchStatus.PHASE1_DONE;
            return ret;
        } catch (TxLockException e) {
            //获取全局锁超时重试
            if((System.currentTimeMillis() - startTime) >= timeout ){
                branchStatus = BranchStatus.PHASE1_TIMEOUT;
                throw new TxLockTimeoutException(timeout, e.getMessage());
            }
            Thread.sleep(3000);
            return doLocalTransaction(xid, callable, argContext, startTime);
        } catch (Exception e){
            branchStatus = BranchStatus.PHASE1_FAIL;
            throw e;
        } finally {
            //上报状态
            if(branchStatus != null){
                report(xid, resourceId, branchStatus);
            }
        }
    }

    private boolean register(String xid, String resourceId, LockKey lockKey, ResourceManager rm) {
        log.info(">>> branch register:xid={0}, resourceId={1}, {2}", xid, resourceId, lockKey.getValue());
        return tc.registerBranch(xid, resourceId, lockKey, rm);
    }

    private void report(String xid, String resourceId, BranchStatus branchStatus) {
        log.info(">>> branch report:xid={0}, resourceId={1}, {2}", xid, resourceId, branchStatus);
        tc.reportBranch(xid, resourceId, branchStatus);
    }

    /**
     * save undolog and commit local transaction
     *
     * 代理DataSource
     * 解析sql，生成执行前后的镜像 即undolog日志
     * undolog日志和业务sql在一个事务中提交
     *
     * @param undoLog
     * @param callable
     * @return
     * @throws Exception
     */
    private Object doInTransaction(UndoLog undoLog, Callable callable, String resourceId) throws Exception {
        log.info(">>> {0} unlog generated: {1}", resourceId, undoLog);
        log.info(">>> {0} commit local db: {1}", resourceId);
        return callable.call();
    }

    /**
     * 二阶段提交
     * @param xid
     * @param resourceId
     * @return
     */
    @Override
    public boolean commitToTc(String xid, String resourceId) {
        //异步删除日志
        log.info(">>>{0} {1} Unlog deleted", xid, resourceId);
        return true;
    }

    /**
     * 二阶段回滚
     */
    @Override
    public boolean rollbackToTc(String xid, String resourceId) {
        //TODO 查询日志，比较当前诗句是否和镜像一致，并执行回滚
        log.info(">>>{0} {1}  rollback by undolog", xid, resourceId);
        return true;
    }

}
