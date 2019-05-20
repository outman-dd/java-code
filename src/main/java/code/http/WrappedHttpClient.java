package code.http;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 〈HttpClient 包装类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
public class WrappedHttpClient implements Closeable{

    @Getter
    private final CloseableHttpClient client;

    public WrappedHttpClient(CloseableHttpClient client) {
        this.client = client;
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
    public WrappedHttpResponse post(String url, HttpRequestConfig config, String json) throws IOException{
        HttpEntity entity = new StringEntity(json, ContentType.create("application/json", config.getCharset()));
        return post(url, config, entity);
    }

    /**
     * Post方式
     *
     * @param url       请求地址
     * @param config    请求配置
     * @param entity    请求内容
     * @return 响应结果
     * @throws IOException
     */
    public WrappedHttpResponse post(String url, HttpRequestConfig config, HttpEntity entity) throws IOException {
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(url);
            post.setConfig(config.requestConfig());

            //entity
            if (entity != null) {
                post.setEntity(entity);
            }

            //headers
            if (config.getHeaders() != null) {
                for (Map.Entry<String, String> entry : config.getHeaders().entrySet()) {
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = client.execute(post);

            return handleResponse(response, config.getCharset());
        } finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (IOException e) {}
        }
    }

    private WrappedHttpResponse handleResponse(CloseableHttpResponse response, String charset) throws IOException {
        InputStream input = null;
        try {
            int status = response.getStatusLine().getStatusCode();
            String data = null;
            if (status == HttpStatus.SC_OK) {
                input = response.getEntity().getContent();
                data = IOUtils.toString(input, charset);
            }
            return new WrappedHttpResponse(status, response.getStatusLine().getReasonPhrase(), data);
        } finally {
            if(input != null){
                try {
                    input.close();
                } catch (IOException e) {}
            }
        }
    }

    @Override
    public void close() throws IOException {
        if(client != null){
            client.close();
        }
    }
}
