package code.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈Netty服务端〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/4/8
 */
public class NettyServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public NettyServer(){
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
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
                                .addLast(new NettyServerHandler());
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