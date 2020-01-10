package code.distribution.at.exception;

import java.text.MessageFormat;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class TxLockTimeoutException extends RuntimeException{

    private static String TIMEOUT_MSG = "Lock reached timeout {0}ms, {1}";

    public TxLockTimeoutException(long timeout, String message) {
        super(MessageFormat.format(TIMEOUT_MSG, timeout, message));
    }

    public TxLockTimeoutException(long timeout, String message, Throwable cause) {
        super(MessageFormat.format(TIMEOUT_MSG, timeout, message), cause);
    }

}
