package code.distribution.raft.client;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-04
 */
@Data
@AllArgsConstructor
public class ClientRet implements Serializable {

    private boolean success;

    private String value;

    private String leaderId;

    public static ClientRet build(boolean success){
        return new ClientRet(success, null, null);
    }

    public static ClientRet buildSuccess(String value){
        return new ClientRet(true, value, null);
    }

    public static ClientRet buildRedirect(String leaderId){
        return new ClientRet(false, null, leaderId);
    }

}
