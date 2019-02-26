package code.distribution.transcation.test;

import code.distribution.transcation.common.ArgContext;
import code.distribution.transcation.common.RoleType;
import code.distribution.transcation.rm.ResourceManager;
import code.distribution.transcation.rm.SimpleResourceManager;
import code.distribution.transcation.utils.Log;

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

    private String bizType = "BranchServiceA.createOrder";

    private String tableName = "ts_order";

    private String keyName = "amount";

    public String createOrder(String xid, String amount) throws Exception {
        Object[] args = new Object[2];
        String ordNo = "ORD"+ (System.currentTimeMillis() + new Random().nextInt(100));
        args[0] = amount;
        return (String)rm.doLocalTransaction(xid, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return create(ordNo, amount);
            }
        }, new ArgContext(bizType, tableName, keyName, ordNo, args));
    }

    private String create(String orderNo, String amount){
        log.info("Create order {0}, amount {1}", orderNo, amount);
        return orderNo;
    }
}
