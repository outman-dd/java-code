package code.distribution.tcc.common;

import code.distribution.at.common.GlobalStatus;
import code.distribution.at.utils.Xid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/7
 */
@Data
public class GlobalTx {

    private String xid;

    private GlobalState state;

    private List<BranchTx> branches;

    public GlobalTx() {
        xid = Xid.newXid();
        state = GlobalState.PROCESSING;
        branches = new ArrayList<>(4);
    }

}
