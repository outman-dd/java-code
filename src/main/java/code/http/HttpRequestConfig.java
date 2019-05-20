package code.http;

import lombok.Data;
import org.apache.http.client.config.RequestConfig;

import java.util.Map;

/**
 * 〈HttpRequestConfig〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
@Data
public class HttpRequestConfig {

    private static int TIMEOUT = 3000;

    private static String UTF8 = "utf-8";

    private static HttpRequestConfig INSTANCE = new HttpRequestConfig();

    /**
     * 从连接池中获取可用连接超时时间
     * 尝试从连接池中获取，若是在等待了一定的时间后还没有获取到可用连接，则会抛出获取连接超时异常。
     */
    private int connectionRequestTimeout = TIMEOUT;

    /**
     * 连接目标超时时间
     * 指的是连接目标url的连接超时时间，即客服端发送请求到与目标url建立起连接的最大时间。
     * 如果在该时间范围内还没有建立起连接，则就抛出ConnectionTimeOut异常。
     */
    private int connectTimeout = TIMEOUT;

    /**
     * 等待响应超时（读取数据超时）
     * 连接上一个url后，获取response的返回等待时间 ，即在与目标url建立连接后，等待放回response的最大时间，
     * 在规定时间内没有返回响应的话就抛出SocketTimeout异常。
     */
    private int socketTimeout = TIMEOUT<<1;

    private String charset = UTF8;

    private Map<String, String> headers;

    public HttpRequestConfig(String charset, int timeout) {
        this.charset = charset;
        this.socketTimeout = timeout;
    }

    public HttpRequestConfig() {

    }

    public RequestConfig requestConfig(){
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout).build();
    }


    public static HttpRequestConfig getDefault(){
        return INSTANCE;
    }

}
