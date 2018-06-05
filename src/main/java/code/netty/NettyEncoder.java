package code.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty编码器<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 16/9/27
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class NettyEncoder extends MessageToByteEncoder<RpcCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, RpcCommand rpcCommand, ByteBuf out)
            throws Exception {
        try {
            out.writeBytes(rpcCommand.encode());
        } catch (Exception e) {
            LOGGER.error("encode exception, " + rpcCommand, e);
        }
    }
}
