package code.distribution.transcation.test;

import code.distribution.transcation.tm.DefaultTransactionManager;
import code.distribution.transcation.tm.TransactionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class GlobalServiceTest {

    private TransactionManager tm = DefaultTransactionManager.getInstance();

    private BranchServiceA branchServiceA = new BranchServiceA();

    private BranchServiceB branchServiceB = new BranchServiceB();

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

    public static void main(String[] args) {
        //singleThreadTest();
        concurrentTest();
    }

    public static void singleThreadTest(){
        GlobalServiceTest serviceTest = new GlobalServiceTest();
        serviceTest.doBiz();
    }

    public static void concurrentTest(){
        GlobalServiceTest serviceTest = new GlobalServiceTest();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int i = 0; i < 2; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    serviceTest.doBiz();
                }
            });
        }
        executorService.shutdown();
    }

}
