package code.netty;

import code.util.NamedThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.net.SocketAddress;
import java.util.Iterator;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-05
 */
public class NettyClient {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroupWorker;
    private final DefaultEventExecutorGroup defaultEventExecutorGroup;

    public NettyClient() {
        this.bootstrap = new Bootstrap();
        this.eventLoopGroupWorker = new NioEventLoopGroup(1, new NamedThreadFactory("EventLoop"));
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(10, new NamedThreadFactory("EventLoopGroupWorker"));

        start();
    }

    public void start() {
        this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 6000)
                .option(ChannelOption.SO_SNDBUF, 20480)
                .option(ChannelOption.SO_RCVBUF, 20480)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                defaultEventExecutorGroup,
                                new NettyEncoder(),
                                new NettyDecoder(),
                                new IdleStateHandler(0, 0, 30),
                                new NettyConnectManageHandler(),
                                new NettyClientHandler());
                    }
                });
    }

    public void destroy() {
//        Iterator<Channel> iterator = channelMap.values().iterator();
//        while (iterator.hasNext()){
//            iterator.next().close();
//        }
        eventLoopGroupWorker.shutdownGracefully();
        defaultEventExecutorGroup.shutdownGracefully();
    }


    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise)
                throws Exception {
            final String local = localAddress == null ? "UNKNOW" : localAddress.toString();
            final String remote = remoteAddress == null ? "UNKNOW" : remoteAddress.toString();
            System.out.println("NETTY CLIENT PIPELINE: CONNECT  " + local + " => " + remote);
            super.connect(ctx, remoteAddress, localAddress, promise);
        }


        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            System.out.println("NETTY CLIENT PIPELINE: DISCONNECT " + remoteAddress);
            RemoteHelper.closeChannel(ctx.channel());
            super.disconnect(ctx, promise);
        }


        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            System.out.println("NETTY CLIENT PIPELINE: CLOSE " + remoteAddress);
            RemoteHelper.closeChannel(ctx.channel());
            super.close(ctx, promise);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
                    System.out.println("NETTY CLIENT PIPELINE: IDLE exception " + remoteAddress);
                    RemoteHelper.closeChannel(ctx.channel());
                }
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            System.out.println("NETTY CLIENT PIPELINE: exceptionCaught " + remoteAddress);
            cause.printStackTrace();
            RemoteHelper.closeChannel(ctx.channel());
        }

    }
}
