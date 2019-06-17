package code.http;

import org.apache.http.conn.UnsupportedSchemeException;
import org.junit.Test;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
public class HttpClientTest {

    private WrappedHttpClient wrappedHttpClient;

    @Test
    public void sslTest() throws IOException {
        wrappedHttpClient = HttpClientBuilder.custom().ssl().build();
        String url = "https://www.baidu.com";
        WrappedHttpResponse response = post(url, "hello");
        System.out.println(response);
    }

    @Test
    public void noSslTest() throws IOException {
        wrappedHttpClient = HttpClientBuilder.custom().build();
        String url = "https://www.baidu.com";
        try {
            WrappedHttpResponse response = post(url, "hello");
            System.out.println(response);
        } catch (Exception e) {
           if(e instanceof UnsupportedSchemeException){
               System.out.println(e.getMessage());
           }else {
               throw e;
           }
        }
    }

    @Test
    public void verifyHostTest() throws IOException {
        wrappedHttpClient = HttpClientBuilder.custom().ssl().verifyHostname(true).build();
        String url = "https://127.0.0.1:8443/hello";
        try {
            WrappedHttpResponse response = post(url, "hello");
            System.out.println(response);
        } catch (Exception e) {
            if(e instanceof SSLPeerUnverifiedException){
                System.out.println(e.getMessage());
            }else {
                throw e;
            }
        }
    }

    @Test
    public void disableVerifyHostTest() throws IOException {
        wrappedHttpClient = HttpClientBuilder.custom().ssl().verifyHostname(false).build();
        String url = "https://127.0.0.1:8443/hello";
        WrappedHttpResponse response = post(url, "hello");
        System.out.println(response);
    }

    @Test
    public void trustStore() throws IOException {
        String path = System.getenv("JAVA_HOME")+"/jre/lib/security/cacerts";
        wrappedHttpClient = HttpClientBuilder.custom().ssl(path, "changeit").build();
        String url = "https://www.baidu.com";
        WrappedHttpResponse response = post(url, "hello");
        System.out.println(response);
    }

    @Test
    public void keyStore() throws IOException {
        String path = "/Users/zixiao/keys/tomcat.jks";
        wrappedHttpClient = HttpClientBuilder.custom()
                .ssl2(path, "123456", "123456")
                .verifyHostname(false)
                .build();
        String url = "https://127.0.0.1:8443";
        WrappedHttpResponse response = post(url, "hello");
        System.out.println(response);
    }

    /**
     * POST方式
     *
     * @param url         URL
     * @param content      请求体
     * @return 响应结果
     * @throws IOException
     */
    public WrappedHttpResponse post(String url, String content) throws IOException {
        return wrappedHttpClient.post(url, HttpRequestConfig.getDefault(), content);
    }


}