package code.netty;

import code.serialize.SerializeFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈NettyServerHandler〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/5
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcCommand command) throws Exception {
        handleRequest(command);
    }

    private void handleRequest(RpcCommand command) {
        String requestStr = SerializeFactory.deserialize(command.getSerializeType(), command.getBody(), String.class);
        LOGGER.info("Receive request: ", requestStr);
        return;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage());
        ctx.close();
    }
}
