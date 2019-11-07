package code.netty;

import code.serialize.SerializeFactory;
import code.serialize.SerializeType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/10/16
 */
public class Client {

    public static void main(String[] args) {

        //worker负责读写数据
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            //辅助启动类
            Bootstrap bootstrap = new Bootstrap();

            //设置线程池
            bootstrap.group(worker);

            //设置socket工厂
            bootstrap.channel(NioSocketChannel.class);

            //设置管道
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //获取管道
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //字符串解码器
                    pipeline.addLast(new NettyDecoder());
                    //字符串编码器
                    pipeline.addLast(new NettyEncoder());
                    //处理类
                    pipeline.addLast(new ClientHandler());
                }
            });

            //发起异步连接操作
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 2345)).sync();

            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的退出，释放NIO线程组
            worker.shutdownGracefully();
        }
    }

    private static void sendMessage(Channel channel, String msg){
        byte[] bytes = SerializeFactory.serialize(SerializeType.Hessian, msg);
        RpcCommand request = new RpcCommand(SerializeType.Hessian, bytes);

        channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    System.out.println(">> writeAndFlush ok: " + msg);
                }else{
                    System.out.println(">> writeAndFlush fail.");
                }
            }
        });
    }

    static class ClientHandler extends SimpleChannelInboundHandler<RpcCommand> {

        //接受服务端发来的消息
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RpcCommand msg) throws Exception {
            String str = SerializeFactory.deserialize(msg.getSerializeType(), msg.getBody(), String.class);
            System.out.println("server response ： " + str);
        }

        //异常
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //关闭管道
            ctx.channel().close();
            //打印异常信息
            cause.printStackTrace();
        }

    }

}


