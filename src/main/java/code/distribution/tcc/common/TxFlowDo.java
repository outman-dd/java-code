package code.distribution.tcc.common;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 〈事务控制表〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
@Data
public class TxFlowDo implements Serializable {

    private String txId;

    private String branchId;

    /**
     * 初始，已提交，已回滚
     */
    private TxState state;

    private Date createTime;

    private Date modifyTime;

    public TxFlowDo(String txId, String branchId, TxState state) {
        this.txId = txId;
        this.branchId = branchId;
        this.state = state;
        this.createTime = new Date();
    }

    public static TxFlowDo buildInit(String txId, String branchId){
        return new TxFlowDo(txId, branchId, TxState.INIT);
    }

    public static TxFlowDo buildCommit(String txId, String branchId){
        return new TxFlowDo(txId, branchId, TxState.COMMIT);
    }

    public static TxFlowDo buildRollback(String txId, String branchId){
        return new TxFlowDo(txId, branchId, TxState.ROLLBACK);
    }

    public static void main(String[] args) {
        TxFlowDo txFlowDo = TxFlowDo.buildCommit("txId", "branchId");
        System.out.println(txFlowDo);
    }

}
