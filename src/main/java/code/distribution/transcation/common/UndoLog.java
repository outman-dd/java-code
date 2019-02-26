package code.distribution.transcation.common;

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

    public UndoLog(String xid, String beforeImage, String afterImage) {
        this.xid = xid;
        this.beforeImage = beforeImage;
        this.afterImage = afterImage;
    }
}
