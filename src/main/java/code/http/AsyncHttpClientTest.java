package code.http;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.junit.Test;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
public class AsyncHttpClientTest {

    private AsyncHttpClient asyncHttpClient;

    @Test
    public void sslTest() throws IOException, ExecutionException, InterruptedException {
        asyncHttpClient = HttpClientBuilder.custom().ssl().buildAsync();
        String url = "https://www.baidu.com";
        asyncHttpClient.post(url, HttpRequestConfig.getDefault(), "hello", new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                System.out.printf("Result: "+result);
            }

            @Override
            public void failed(Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void cancelled() {

            }
        });
        Thread.sleep(3000);
        asyncHttpClient.close();
    }

    @Test
    public void noSslTest() throws IOException, ExecutionException, InterruptedException {
        asyncHttpClient = HttpClientBuilder.custom().buildAsync();
        String url = "https://www.baidu.com";
        try {
            Future<HttpResponse> future = post(url, "hello");
            System.out.println(future.get());
        } catch (Exception e) {
           if(e.getMessage().contains("UnsupportedSchemeException")){
               System.out.println(e.getMessage());
           }else {
               throw e;
           }
        }
    }

    @Test
    public void verifyHostTest() throws IOException, ExecutionException, InterruptedException {
        asyncHttpClient = HttpClientBuilder.custom().ssl().verifyHostname(true).buildAsync();
        String url = "https://127.0.0.1:8443/hello";
        try {
            Future<HttpResponse> future = post(url, "hello");
            System.out.println(future.get());
        } catch (Exception e) {
            if(e instanceof SSLPeerUnverifiedException){
                System.out.println(e.getMessage());
            }else {
                throw e;
            }
        }
    }

    @Test
    public void disableVerifyHostTest() throws IOException, ExecutionException, InterruptedException {
        asyncHttpClient = HttpClientBuilder.custom().ssl().verifyHostname(false).buildAsync();
        String url = "https://127.0.0.1:8443/hello";
        Future<HttpResponse> future = post(url, "hello");
        System.out.println(future.get());
    }

    @Test
    public void trustStore() throws IOException, ExecutionException, InterruptedException {
        String path = System.getenv("JAVA_HOME")+"/jre/lib/security/cacerts";
        asyncHttpClient = HttpClientBuilder.custom().ssl(path, "changeit").buildAsync();
        String url = "https://www.baidu.com";
        Future<HttpResponse> future = post(url, "hello");
        System.out.println(future.get());
    }

    @Test
    public void keyStore() throws IOException, ExecutionException, InterruptedException {
        String path = "/Users/zixiao/keys/tomcat.jks";
        asyncHttpClient = HttpClientBuilder.custom()
                .ssl2(path, "123456", "123456")
                .verifyHostname(false)
                .buildAsync();
        String url = "https://127.0.0.1:8443";
        Future<HttpResponse> future = post(url, "hello");
        System.out.println(future.get());
    }

    /**
     * POST方式
     *
     * @param url         URL
     * @param content      请求体
     * @return 响应结果
     * @throws IOException
     */
    public Future<HttpResponse> post(String url, String content) throws IOException {
        return asyncHttpClient.post(url, HttpRequestConfig.getDefault(), content);
    }


}