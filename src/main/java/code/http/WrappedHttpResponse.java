package code.http;

import lombok.Data;

/**
 * 〈HttpResponse 包装类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
public interface WrappedHttpResponse<T> {

    int getStatusCode();

    String getReasonPhrase();

    T getData();
}
