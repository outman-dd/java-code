package code.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 〈Netty服务端〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/4/8
 */
public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * Acceptor线程用于监听服务端，接收客户端的TCP连接请求
     */
    private final EventLoopGroup bossGroup;

    /**
     * 网络IO操作-读、写等处理的线程池
     */
    private final EventLoopGroup workerGroup;

    private final ExecutorService bizExecutor;

    public NettyServer(){
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()+1);
        bizExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(0));
    }

    public void start(int port, String ip) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new NettyEncoder())
                                .addLast(new NettyDecoder())
                                .addLast(new NettyServerHandler(bizExecutor));
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture future = bootstrap.bind(ip, port).sync();
            LOGGER.info("Netty server start success at port:{}.", port);

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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

}