package code.distribution.tcc.rm;

import code.distribution.tcc.common.TxFlowDo;
import code.distribution.tcc.common.TxState;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/8
 */
public class SimpleDb {

    private static final String SEP = "%s@%s";

    private static ConcurrentHashMap<String, TxFlowDo> txFlowTable = new ConcurrentHashMap<>();

    public static TxFlowDo select(String xid, String branchId){
        return txFlowTable.get(String.format(SEP, xid, branchId));
    }

    public static TxFlowDo selectForUpdate(String xid, String branchId){
        return txFlowTable.get(String.format(SEP, xid, branchId));
    }

    public static boolean insert(String xid, String branchId, TxState state){
        TxFlowDo old = txFlowTable.putIfAbsent(String.format(SEP, xid, branchId), new TxFlowDo(xid, branchId, state));
        return old == null;
    }

}
