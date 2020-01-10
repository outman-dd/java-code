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
public class BranchServiceA {

    private Log log = Log.getLog(RoleType.RM);

    private ResourceManager rm = new SimpleResourceManager();

    private String tableName = "order_main";

    public String createOrder(String xid, String amount) throws Exception {
        Object[] args = new Object[2];
        String ordNo = "ORD"+ (System.currentTimeMillis() + new Random().nextInt(100));
        args[0] = amount;
        return (String)rm.doLocalTransaction(xid, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return create(ordNo, amount);
            }
        }, new ArgContext(tableName, "1,2", args));
    }

    private String create(String orderNo, String amount){
        log.info("Create order {0}, amount {1}", orderNo, amount);
        return orderNo;
    }
}
