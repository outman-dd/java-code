package code.http;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 〈HttpClient 构造器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/16
 */
public class HttpClientBuilder {

    /**
     * 是否开启ssl
     */
    private boolean enableSSL = false;

    /**
     * 是否信任所有证书
     * 忽略对服务器端证书合法性校验
     */
    private boolean trustAll = false;

    /**
     * 主机名验证
     * 验证目标主机名是否跟服务端存储在X.509认证里的一致
     */
    private boolean verifyHostname = true;

    /**
     * 最大总连接数
     */
    private int maxTotal = 1024;

    /**
     * 单个路由最大连接数
     */
    private int defaultMaxPerRoute = 8;

    /**
     * 最大空闲时间
     */
    private int maxIdleTime = 5000;

    /**
     * keyStore
     */
    private KeyStore keyStore;

    private String keyPass;

    /**
     * trustStore
     */
    private KeyStore trustStore;

    private ConnectionSocketFactory socketFactory;

    private ConnectionSocketFactory sslSocketFactory;

    public static HttpClientBuilder custom(){
        return new HttpClientBuilder();
    }

    /**
     * 开启双向认证的SSL
     * 服务端需要认证客户端的证书，一般用于双向认证
     * @param keyStorePath
     * @param storePass
     * @param keyPass
     * @return
     */
    public HttpClientBuilder ssl2(String keyStorePath, String storePass, String keyPass){
        this.enableSSL = true;
        this.keyStore = loadKeyStore(keyStorePath, storePass);
        this.keyPass = keyPass;
        return this;
    }

    /**
     * 开启SSL 指定trustStore
     * @param trustStorePath
     * @param trustStorePass
     * @return
     */
    public HttpClientBuilder ssl(String trustStorePath, String trustStorePass){
        this.enableSSL = true;
        this.trustStore = loadTrustStore(trustStorePath, trustStorePass);
        return this;
    }

    /**
     * 开启SSL 使用默认trustStore
     *
     * @return
     */
    public HttpClientBuilder ssl(){
        this.enableSSL = true;
        return this;
    }

    /**
     * 信任所有证书
     * 忽略对服务器端证书合法性校验
     *
     * @return
     */
    public HttpClientBuilder trustAll(){
        this.trustAll = true;
        return this;
    }

    /**
     * 是否主机名验证
     * 当 trustAll=false 时，有效
     * @param verifyFlag
     * @return
     */
    public HttpClientBuilder verifyHostname(boolean verifyFlag){
        this.verifyHostname = verifyFlag;
        return this;
    }

    /**
     * 连接池化
     * @param maxTotal
     * @param defaultMaxPerRoute
     * @return
     */
    public HttpClientBuilder pool(int maxTotal, int defaultMaxPerRoute){
        this.maxTotal = maxTotal;
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        return this;
    }

    /**
     * 连接最大空闲时间，超过会被自动释放
     * @param maxIdleTime
     * @return
     */
    public HttpClientBuilder maxIdleTime(int maxIdleTime){
        this.maxIdleTime = maxIdleTime;
        return this;
    }

    private CloseableHttpClient buildClient(){
        // 创建 ConnectionSocketFactory
        createConnectionSocketFactory();

        // 配置支持的协议
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", socketFactory);
        if(enableSSL && sslSocketFactory != null){
            registryBuilder.register("https", sslSocketFactory);
        }

        // 连接池管理类
        PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager(registryBuilder.build());
        manager.setMaxTotal(maxTotal);
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        return HttpClients.custom()
                .setConnectionManager(manager)
                .evictExpiredConnections()
                .evictIdleConnections(maxIdleTime, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * 构造池化的HttpClient
     *
     * @return
     */
    public WrappedHttpClient build(){
        return new WrappedHttpClient(buildClient());
    }

    private void createConnectionSocketFactory(){
        //Http
        this.socketFactory = PlainConnectionSocketFactory.getSocketFactory();

        //Https
        if(enableSSL){
            SSLContext sslContext = createSSLContext();

            HostnameVerifier hostnameVerifier = null;
            if(verifyHostname){
                //开启主机名验证，https://publicsuffix.org/list
                hostnameVerifier = SSLConnectionSocketFactory.getDefaultHostnameVerifier();
            }else {
                //使用 NoopHostnameVerifier 关闭主机名验证
                hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            }

            this.sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        }
    }


    /**
     * 创建SSL上下文
     * @return
     */
    private SSLContext createSSLContext(){
        SSLContextBuilder sslContextBuilder = SSLContexts.custom();
        try {
            if(keyStore != null){
                // 携带客户端证书
                sslContextBuilder.loadKeyMaterial(keyStore, keyPass.toCharArray(), new PrivateKeyStrategy() {
                    @Override
                    public String chooseAlias(Map<String, PrivateKeyDetails> aliases, Socket socket) {
                        aliases.entrySet().forEach(entry -> {
                            System.out.println(entry.getKey());
                        });
                        return "test";
                    }
                });
            }
            if(trustAll){
                // 信任所有证书
                sslContextBuilder.loadTrustMaterial(trustStore, TrustAllStrategy.INSTANCE);
            }else {
                // 信任自己的CA和所有自签名的证书
                sslContextBuilder.loadTrustMaterial(trustStore, TrustSelfSignedStrategy.INSTANCE);
            }
            return sslContextBuilder.build();
        } catch (Exception e) {
            throw new HttpClientException("创建SSL上下文异常", e);
        }
    }

    /**
     * 加载信任证书库
     *
     * ﻿A TrustStore contains only the certificates trusted by the client (a “trust” store).
     * These certificates are CA root certificates, that is, self-signed certificates.
     *
     * @param storePath
     * @param storePass
     * @return
     * @throws HttpClientException
     */
    private KeyStore loadTrustStore(String storePath, String storePass) throws HttpClientException {
        FileInputStream fis = null;
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fis = new FileInputStream(new File(storePath));
            trustStore.load(fis, storePass.toCharArray());
            return trustStore;
        } catch (Exception e) {
            throw new HttpClientException("加载trustStore失败", e);
        } finally{
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {}
            }
        }
    }

    /**
     * 加载KeyStore
     * 包括私钥，客户端证书，CA证书链
     * ﻿A KeyStore consists of a database containing a private key and an associated certificate, or an associated certificate chain.
     * The certificate chain consists of the client certificate and one or more certification authority (CA) certificates.
     *
     * @param storePath
     * @param storePass
     * @return
     * @throws HttpClientException
     */
    private KeyStore loadKeyStore(String storePath, String storePass) throws HttpClientException {
        FileInputStream fis = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fis = new FileInputStream(new File(storePath));
            keyStore.load(fis, storePass.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new HttpClientException("加载keyStore失败", e);
        } finally{
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {}
            }
        }
    }

}
