package code.distribution.at.tc;

import code.distribution.at.common.GlobalStatus;
import code.distribution.at.utils.Xid;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class GlobalSession {

    private String xid;

    private GlobalStatus globalStatus;

    public GlobalSession() {
        xid = Xid.newXid();
        globalStatus = GlobalStatus.PROCESSING;
    }

    public String getXid() {
        return xid;
    }

    public GlobalStatus getGlobalStatus() {
        return globalStatus;
    }

    public void setGlobalStatus(GlobalStatus globalStatus) {
        this.globalStatus = globalStatus;
    }

    @Override
    public String toString() {
        return "GlobalSession{" +
                "xid='" + xid + '\'' +
                ", globalStatus=" + globalStatus +
                '}';
    }
}
