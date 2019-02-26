package code.distribution.transcation.tm;

import code.distribution.transcation.common.RoleType;
import code.distribution.transcation.tc.DefaultTransactionCoordinator;
import code.distribution.transcation.tc.TransactionCoordinator;
import code.distribution.transcation.utils.Log;

/**
 * 〈事务管理器〉<p>
 * 〈定义全局事务作用域，负责全局事务的开启，提交，回滚〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class DefaultTransactionManager implements TransactionManager {

    private static Log log = Log.getLog(RoleType.TM);

    private static TransactionManager instance = new DefaultTransactionManager();

    private TransactionCoordinator tc = DefaultTransactionCoordinator.getInstance();

    private DefaultTransactionManager(){}

    public static TransactionManager getInstance() {
        return instance;
    }

    @Override
    public String begin() {
        String xid = tc.beginGlobal();
        log.info("Global tx begin, xid=" + xid);
        return xid;
    }

    @Override
    public boolean commit(String xid) {
        log.info("Global tx commit, xid=" + xid);
        return tc.commitGlobal(xid);
    }

    @Override
    public boolean rollback(String xid) {
        log.info("Global tx rollback, xid=" + xid);
        return tc.rollbackGlobal(xid);
    }
}
