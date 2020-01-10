package code.distribution.at.test;

import code.distribution.at.common.RoleType;
import code.distribution.at.tm.AtTransactionManager;
import code.distribution.at.tm.TransactionManager;
import code.distribution.at.utils.Log;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class GlobalService {

    private TransactionManager tm = AtTransactionManager.getInstance();

    private BranchServiceA branchServiceA = new BranchServiceA();

    private BranchServiceB branchServiceB = new BranchServiceB();

    private Log log = Log.getLog(RoleType.TM);

    @Test
    public void singleThreadTest(){
        doBiz();
    }

    @Test
    public void concurrentTest(){
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executorService.execute(() -> doBiz());
        }
        executorService.shutdown();
    }

    public void doBiz(){
        //1、开启事务
        String xid = tm.begin();

        //2、业务逻辑
        try {
            String ordNo = branchServiceA.createOrder(xid, "100.00");
            branchServiceB.pay(xid, ordNo, "100.00");
            //提交事务
            tm.commit(xid);
        } catch (Exception e) {
            e.printStackTrace();
            //回滚事务
            tm.rollback(xid);
        }
    }


}
