package code.distribution.raft.rpc;

import code.distribution.raft.util.RaftSerializeUtils;
import code.http.ByteHttpResponse;
import code.http.HttpClientBuilder;
import code.http.HttpRequestConfig;
import code.http.WrappedHttpClient;

import java.io.IOException;

/**
 * 〈RpcHttpClient〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-06
 */
public class RpcHttpClient {

    private final WrappedHttpClient wrappedHttpClient;

    public RpcHttpClient() {
        wrappedHttpClient = HttpClientBuilder.custom().build();
    }

    /**
     * 发送post请求
     *
     * @param nodeId
     * @param raftRequest
     * @return RaftResponse
     */
    public RaftResponse post(String nodeId, RaftRequest raftRequest) {
        ByteHttpResponse byteHttpResponse = postInner(nodeId, raftRequest);
        if (byteHttpResponse.getStatusCode() == 200) {
            return RaftSerializeUtils.deserialize(byteHttpResponse.getData(), RaftResponse.class);
        } else {
            return RaftResponse.buildFail(byteHttpResponse.getReasonPhrase());
        }
    }

    /**
     * POST方式
     *
     * @param nodeId  nodeId
     * @param content 请求体
     * @return ByteHttpResponse
     */
    private ByteHttpResponse postInner(String nodeId, Object content) {
        try {
            String url = "http://" + nodeId;
            byte[] bytes = RaftSerializeUtils.serialize(content);
            return (ByteHttpResponse) wrappedHttpClient.post(url, HttpRequestConfig.getDefault(), bytes);
        } catch (IOException e) {
            return new ByteHttpResponse(400, e.getMessage(), null);
        }
    }

    public static void main(String[] args) {
        ByteHttpResponse response = new RpcHttpClient().postInner("127.0.0.1:2000", "abc");
        String respString = RaftSerializeUtils.deserialize(response.getData(), String.class);
        System.out.println("Resp:" + respString);
    }
}
