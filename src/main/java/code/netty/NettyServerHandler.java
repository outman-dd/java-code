package code.netty;

import code.serialize.SerializeFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * 〈NettyServerHandler〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/5
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServerHandler.class);

    private ExecutorService bizExecutor;

    public NettyServerHandler(ExecutorService bizExecutor) {
        this.bizExecutor = bizExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcCommand command) throws Exception {
        handleRequest(ctx, command);
    }

    private void handleRequest(ChannelHandlerContext ctx, RpcCommand command) {
        try {
            bizExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    String requestStr = SerializeFactory.deserialize(command.getSerializeType(), command.getBody(), String.class);
                    LOGGER.info("Receive request: ", requestStr);
                    ctx.writeAndFlush(requestStr);
                }
            });
        } catch (RejectedExecutionException e) {
            ctx.writeAndFlush("Service is busy.");
        } catch (Exception e){
            ctx.writeAndFlush("Receive error:"+e.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage());
        ctx.close();
    }
}
