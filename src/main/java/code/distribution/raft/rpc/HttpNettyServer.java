package code.distribution.raft.rpc;

import code.distribution.raft.RaftNodeServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈Netty服务端〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/4/8
 */
public class HttpNettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpNettyServer.class);

    /**
     * Acceptor线程用于监听服务端，接收客户端的TCP连接请求
     */
    private final EventLoopGroup bossGroup;

    /**
     * 网络IO操作-读、写等处理的线程池
     */
    private final EventLoopGroup workerGroup;

    private final RaftNodeServer nodeServer;

    public HttpNettyServer(RaftNodeServer nodeServer) {
        this.nodeServer = nodeServer;
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() + 1);
    }

    public void start() {
        String[] addrArray = nodeServer.getNode().getNodeId().split(":");
        String ip = addrArray[0];
        int port = Integer.parseInt(addrArray[1]);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new HttpRequestDecoder())
                                .addLast(new HttpResponseEncoder())
                                .addLast(new HttpObjectAggregator(65536))
                                .addLast(new HttpNettyServerHandler(nodeServer));
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            LOGGER.info("Netty server start success at port:{}.", port);

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
        } finally {
            destroy();
        }
    }

    public void destroy() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new HttpNettyServer(null).start();
    }

}