package code.designpattern.chain;

/**
 * 〈响应〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/2/28
 */
public class Response {

    private StringBuffer response = new StringBuffer();

    public StringBuffer getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "Response{" +
                "response='" + response.toString() + '\'' +
                '}';
    }

}
