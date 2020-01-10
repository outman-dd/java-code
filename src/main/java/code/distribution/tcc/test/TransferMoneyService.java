package code.distribution.tcc.test;

import code.distribution.tcc.tm.TccTxManager;
import code.distribution.tcc.tm.TxManager;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
public class TransferMoneyService {

    private ReduceAccountService reduceAccountService = new ReduceAccountService();

    private AddAccountService addAccountService = new AddAccountService();

    private TxManager tm = TccTxManager.getInstance();


    @Test
    public void testConfirm(){
        transfer(getOrderNo(), 1, 13, new BigDecimal("50.00"));
    }

    @Test
    public void testCancel(){
        transfer(getOrderNo(), 1, 13, new BigDecimal("100.00"));
    }

    public boolean transfer(String orderNo, int fromId, int toId, BigDecimal amount){
        //0 开启全局事务
        String xid = tm.begin();
        try {
            //1 调用减钱服务
            reduceAccountService.reduce(xid, orderNo, fromId, amount);
            //2 调用加钱服务
            addAccountService.add(xid, orderNo, toId, amount);
            //3 事务提交
            tm.commit(xid);

            System.out.println(String.format(">>>账户[%d]->账户[%s]转账 %s 元成功", fromId, toId, amount));
            return true;
        } catch (Exception e) {
            System.out.println(String.format(">>>账户[%d]->账户[%s]转账 %s 元失败", fromId, toId, amount));
            e.printStackTrace();
            //3 事务回滚
            tm.rollback(xid);
            return false;
        }
    }

    private String getOrderNo(){
        return UUID.randomUUID().toString().substring(0,8);
    }

}
