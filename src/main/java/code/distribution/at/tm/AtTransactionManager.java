package code.distribution.at.tm;

import code.distribution.at.common.RoleType;
import code.distribution.at.tc.AtTransactionCoordinator;
import code.distribution.at.tc.TransactionCoordinator;
import code.distribution.at.utils.Log;

/**
 * 〈事务管理器〉<p>
 * 〈定义全局事务作用域，负责全局事务的开启，提交，回滚〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class AtTransactionManager implements TransactionManager {

    private static Log log = Log.getLog(RoleType.TM);

    private static TransactionManager instance = new AtTransactionManager();

    private TransactionCoordinator tc = AtTransactionCoordinator.getInstance();

    private AtTransactionManager(){}

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
