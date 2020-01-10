package code.distribution.tcc.test;

import code.distribution.tcc.common.TccAction;
import code.distribution.tcc.common.TxMethod;
import code.distribution.tcc.rm.ResManager;
import code.distribution.tcc.rm.TccResManager;
import code.distribution.tcc.utils.TccUtils;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
public class ReduceAccountService {

    private ResManager rm = TccResManager.getInstance();

    private String branchId = "ReduceAccountService.reduce";

    @TccAction(try1 = "reduceTry", confirm = "reduceConfirm", cancel = "reduceCancel")
    public void reduce(String xid, String orderNo, int accountId, BigDecimal amount){
        TxMethod txMethod = TccUtils.getTxMethod(this, getClass(), "reduce", new Object[]{xid, orderNo, accountId, amount});
        if(txMethod != null){
            rm.registerBranch(xid, branchId, txMethod);
        }

        reduceTry(xid, orderNo, accountId, amount);
    }

    /**
     * 减钱Try方法， 预留资源，冻结部分可用余额，即减少可用余额，增加冻结金额
     * @param orderNo
     * @param accountId
     * @param amount
     * @return
     */
    public void reduceTry(String xid, String orderNo, int accountId, BigDecimal amount){
        if(!rm.beforeTry(xid, branchId)){
            return;
        }

        System.out.println(String.format("reduceTry：成功冻结账户 %d, %s元，orderNo=%s", accountId, amount,orderNo));

        rm.onePhase(xid, branchId);
    }

    /**
     * 减钱confirm方法，直接将冻结金额扣除
     * @param orderNo
     * @param accountId
     * @param amount
     * @return
     */
    public void reduceConfirm(String xid, String orderNo, int accountId, BigDecimal amount){
        if(!rm.beforeConfirm(xid, branchId)){
            return;
        }

        System.out.println(String.format("reduceConfirm：成功扣除账户 %d, %s元，orderNo=%s", accountId, amount, orderNo));

        rm.commit(xid, branchId);
    }

    /**
     * 减钱cancel方法，冻结金额解冻到可用余额
     * @param orderNo
     * @param accountId
     * @param amount
     * @return
     */
    public void reduceCancel(String xid, String orderNo, int accountId, BigDecimal amount){
        if(!rm.beforeCancel(xid, branchId)){
            return;
        }

        System.out.println(String.format("reduceCancel：成功回滚账户 %d, %s元， orderNo=%s", accountId, amount, orderNo));

        rm.rollback(xid, branchId);
    }

}
