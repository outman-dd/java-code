package code.netty;

import code.serialize.SerializeFactory;
import code.serialize.SerializeType;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
    protected void channelRead0(ChannelHandlerContext ctx, RpcCommand request) throws Exception {
        String requestStr = SerializeFactory.deserialize(request.getSerializeType(), request.getBody(), String.class);
        LOGGER.info("Receive request: {}", requestStr);
        handleRequest(ctx, request.getSerializeType(), requestStr);
    }

    private void handleRequest(ChannelHandlerContext ctx, SerializeType serializeType, String requestStr) {
        try {
            bizExecutor.execute(() -> {
                String resp = "Hello " + requestStr;
                writeResponse(ctx, serializeType, resp);
            });
        } catch (RejectedExecutionException e) {
            LOGGER.error("Service is busy.");
            writeResponse(ctx, serializeType, "Service is busy.");
        } catch (Exception e) {
            LOGGER.error("Receive error:", e);
            writeResponse(ctx, serializeType, "Receive error:" + e.getMessage());
        }
    }

    private void writeResponse(ChannelHandlerContext ctx, SerializeType serializeType, String responseStr) {
        byte[] respBytes = SerializeFactory.serialize(serializeType, responseStr);
        RpcCommand response = new RpcCommand(serializeType, respBytes);
        ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println(">> writeAndFlush ok.");
                } else {
                    System.err.println(">> writeAndFlush fail.");
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error(cause.getMessage());
        ctx.close();
    }
}
