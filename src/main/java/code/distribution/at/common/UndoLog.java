package code.distribution.at.common;

import lombok.Data;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
@Data
public class UndoLog {

    private String xid;

    private String beforeImage;

    private String afterImage;

    public UndoLog(String xid) {
        this.xid = xid;
    }
}
