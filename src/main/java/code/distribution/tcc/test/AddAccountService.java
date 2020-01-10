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
public class AddAccountService {

    private ResManager rm = TccResManager.getInstance();

    private String branchId = "AddAccountService.add";

    private BigDecimal balance = new BigDecimal("99.00");

    @TccAction(try1 = "addTry", confirm = "addConfirm", cancel = "addCancel")
    public boolean add(String xid, String orderNo, int accountId, BigDecimal amount){
        TxMethod txMethod = TccUtils.getTxMethod(this, getClass(), "add", new Object[]{xid, orderNo, accountId, amount});
        if(txMethod != null){
            rm.registerBranch(xid, branchId, txMethod);
        }

        addTry(xid, orderNo, accountId, amount);
        return true;
    }

    /**
     * 加钱try方法，空操作
     * @param orderNo
     * @param accountId
     * @param amount
     * @return
     */
    public void addTry(String xid, String orderNo, int accountId, BigDecimal amount){
        if(!rm.beforeTry(xid, branchId)){
            return;
        }

        if(amount.compareTo(balance) > 0){
            throw new RuntimeException("余额不足");
        }
        System.out.println(String.format("addTry：空操作，orderNo=%s", orderNo));

        rm.onePhase(xid, branchId);
    }

    /**
     * 加钱confirm方法，执行账户加钱操作
     * @param orderNo
     * @param accountId
     * @param amount
     * @return
     */
    public void addConfirm(String xid, String orderNo, int accountId, BigDecimal amount){
        if(!rm.beforeConfirm(xid, branchId)){
            return;
        }

        System.out.println(String.format("addConfirm：成功给账户 %d 加钱 %s 元, orderNo=%s", accountId, amount, orderNo));

        rm.commit(xid, branchId);
    }

    /**
     * 加钱cancel方法，空操作
     * @param orderNo
     * @param accountId
     * @param amount
     * @return
     */
    public void addCancel(String xid, String orderNo, int accountId, BigDecimal amount){
        if(!rm.beforeCancel(xid, branchId)){
            return;
        }

        System.out.println(String.format("addCancel：空操作，orderNo=%s", orderNo));

        rm.rollback(xid, branchId);
    }
}
