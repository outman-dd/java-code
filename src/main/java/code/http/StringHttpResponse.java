package code.http;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-05
 */
@Data
@AllArgsConstructor
public class StringHttpResponse implements WrappedHttpResponse{

    private int statusCode;

    private String reasonPhrase;

    private String data;

}
