package code.netty;

import code.serialize.SerializeFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-05
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcCommand> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RpcCommand msg) throws Exception {
            String str = SerializeFactory.deserialize(msg.getSerializeType(), msg.getBody(), String.class);
            System.out.println("server response ： " + str);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = RemoteHelper.parseChannelRemoteAddr(ctx.channel());
            System.out.println("NettyClientHandler read exception " + remoteAddress);
            cause.printStackTrace();
        }


}
