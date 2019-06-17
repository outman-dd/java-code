package code.http;

import lombok.Getter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Future;

import static code.http.HttpClientBuilder.IdleConnectionEvictor;

/**
 * 〈异步HttpClient〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/17
 */
public class AsyncHttpClient implements Closeable {

    @Getter
    private final CloseableHttpAsyncClient client;

    private IdleConnectionEvictor connEvictor;

    public AsyncHttpClient(CloseableHttpAsyncClient client) {
        this.client = client;
    }

    public AsyncHttpClient(CloseableHttpAsyncClient client, IdleConnectionEvictor connEvictor) {
        this.client = client;
        this.connEvictor = connEvictor;
    }

    /**
     * Post方式
     *
     * @param url       请求地址
     * @param config    请求配置
     * @param json      json格式字符串
     * @param callback  回调方法
     * @return
     * @throws IOException
     */
    public Future<HttpResponse> post(String url, HttpRequestConfig config, String json, FutureCallback<HttpResponse> callback) throws IOException{
        HttpEntity entity = new StringEntity(json, ContentType.create("application/json", config.getCharset()));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);

        return client.execute(httpPost, callback);
    }

    /**
     * Post方式
     *
     * @param url       请求地址
     * @param config    请求配置
     * @param json      json格式字符串
     * @return
     * @throws IOException
     */
    public Future<HttpResponse> post(String url, HttpRequestConfig config, String json) throws IOException{
        HttpEntity entity = new StringEntity(json, ContentType.create("application/json", config.getCharset()));

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);

        return post(url, config, json, null);
    }

    @Override
    public void close() throws IOException {
        if(connEvictor != null){
            connEvictor.shutdown();
        }
        if(client != null){
            client.close();
        }
    }

}
