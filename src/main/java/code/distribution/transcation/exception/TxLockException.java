package code.distribution.transcation.exception;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class TxLockException extends RuntimeException{

    public TxLockException(String message) {
        super(message);
    }

    public TxLockException(String message, Throwable cause) {
        super(message, cause);
    }

}
