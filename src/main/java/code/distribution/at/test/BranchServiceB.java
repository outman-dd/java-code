package code.distribution.at.test;

import code.distribution.at.common.ArgContext;
import code.distribution.at.common.RoleType;
import code.distribution.at.rm.ResourceManager;
import code.distribution.at.rm.SimpleResourceManager;
import code.distribution.at.utils.Log;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class BranchServiceB {

    private Log log = Log.getLog(RoleType.RM);

    private ResourceManager rm = new SimpleResourceManager();

    private String tableName = "pay_order";

    public void pay(String xid, String ordNo, String amount) throws Exception {
        Object[] args = new Object[2];
        args[0] = amount;
        rm.doLocalTransaction(xid, new Callable() {
            @Override
            public Object call() throws Exception {
                return create(ordNo, amount);
            }
        }, new ArgContext(tableName, ordNo, args));
    }

    private Object create(String orderNo, String amount) throws InterruptedException {
        log.info("pay order {0}, amount {1}", orderNo, amount);
        Thread.sleep(new Random().nextInt(3)*1000);
        return "Ok";
        //throw new RuntimeException("pay timeout");
    }

}
