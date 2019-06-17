package code.http;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
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

    /**
     * 空闲连接销毁线程
     */
    private IdleConnectionEvictor connEvictor;

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

    /**
     * 构造同步HttpClient
     *
     * @return
     */
    public WrappedHttpClient build(){
        return new WrappedHttpClient(buildSyncClient());
    }

    /**
     * 构建异步HttpClient
     *
     * @return
     */
    public AsyncHttpClient buildAsync(){
        CloseableHttpAsyncClient client = buildAsyncClient();
        client.start();
        if(connEvictor != null){
            connEvictor.start();
        }
        return new AsyncHttpClient(client, connEvictor);
    }

    /**
     * 构建同步Client
     * @return
     */
    private CloseableHttpClient buildSyncClient(){
        // 配置支持的协议
        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory());
        if(enableSSL){
            registryBuilder.register("https", createSSLSocketFactory());
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

    private ConnectionSocketFactory createSSLSocketFactory(){
        return new SSLConnectionSocketFactory(createSSLContext(), createHostnameVerifier());
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
                sslContextBuilder.loadKeyMaterial(keyStore, keyPass.toCharArray());
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
     * 创建主机名验证器
     * @return
     */
    private HostnameVerifier createHostnameVerifier(){
        if(verifyHostname){
            //开启主机名验证，https://publicsuffix.org/list
            return SSLConnectionSocketFactory.getDefaultHostnameVerifier();
        }else {
            //使用 NoopHostnameVerifier 关闭主机名验证
            return NoopHostnameVerifier.INSTANCE;
        }
    }

    /**
     * 构建异步Client
     * @return
     */
    private CloseableHttpAsyncClient buildAsyncClient() {
        // 配置支持的协议
        RegistryBuilder<SchemeIOSessionStrategy> registryBuilder = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register("http", NoopIOSessionStrategy.INSTANCE);
        if(enableSSL){
            registryBuilder.register("https", createSSLIOSessionStrategy());
        }

        ConnectingIOReactor ioReactor = createIOReactor();
        PoolingNHttpClientConnectionManager manager = new PoolingNHttpClientConnectionManager(ioReactor, registryBuilder.build());
        manager.setMaxTotal(maxTotal);
        manager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        connEvictor = new IdleConnectionEvictor(manager, maxIdleTime);

        return HttpAsyncClients.custom()
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                .setConnectionManager(manager)
                .build();
    }

    private SchemeIOSessionStrategy createSSLIOSessionStrategy(){
        return new SSLIOSessionStrategy(createSSLContext(), createHostnameVerifier());
    }

    private ConnectingIOReactor createIOReactor(){
        //配置io线程
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount(Runtime.getRuntime().availableProcessors())
                .setSoKeepAlive(true)
                .build();
        try {
            return new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            throw new HttpClientException("Create IOReactor failed", e);
        }
    }

    public static class IdleConnectionEvictor extends Thread {

        private final NHttpClientConnectionManager connMgr;

        private final long maxIdleTime;

        private volatile boolean shutdown;

        public IdleConnectionEvictor(NHttpClientConnectionManager connMgr, long maxIdleTime) {
            super();
            this.connMgr = connMgr;
            this.maxIdleTime = maxIdleTime;
        }

        @Override
        public void run() {
            try {
                while (!shutdown) {
                    synchronized (this) {
                        wait(maxIdleTime);
                        // Close expired connections
                        connMgr.closeExpiredConnections();
                        // Optionally, close connections
                        // that have been idle longer than 5 sec
                        connMgr.closeIdleConnections(maxIdleTime, TimeUnit.MILLISECONDS);
                    }
                }
            } catch (InterruptedException ex) {
                // terminate
            }
        }

        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
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
