package code.http;

/**
 * 〈HttpClientException〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
public class HttpClientException extends RuntimeException {

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
