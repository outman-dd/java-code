package code.http;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 〈HttpResponse 包装类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
@Data
@AllArgsConstructor
public class WrappedHttpResponse {

    private int statusCode;

    private String reasonPhrase;

    private String data;

}
