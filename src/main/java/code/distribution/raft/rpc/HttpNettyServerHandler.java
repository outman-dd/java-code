package code.distribution.raft.rpc;

import code.distribution.raft.RaftNodeServer;
import code.distribution.raft.client.ClientReq;
import code.distribution.raft.client.ClientRet;
import code.distribution.raft.enums.RequestType;
import code.distribution.raft.model.AppendEntriesReq;
import code.distribution.raft.model.AppendEntriesRet;
import code.distribution.raft.model.RequestVoteReq;
import code.distribution.raft.model.RequestVoteRet;
import code.distribution.raft.util.RaftSerializeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.rtsp.RtspHeaderNames.CONTENT_LENGTH;

/**
 * 〈HttpNettyServerHandler〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-05
 */
public class HttpNettyServerHandler extends ChannelInboundHandlerAdapter {

    private final RaftNodeServer nodeServer;

    public HttpNettyServerHandler(RaftNodeServer nodeServer) {
        this.nodeServer = nodeServer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            HttpContent content = (HttpContent) msg;
            ByteBuf buf = content.content();
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            buf.release();

            RaftRequest raftRequest = RaftSerializeUtils.deserialize(req, RaftRequest.class);

            if (raftRequest.getRequestType() == RequestType.APPEND_ENTRIES) {
                AppendEntriesReq appendEntriesReq = (AppendEntriesReq) raftRequest.getRequest();
                AppendEntriesRet appendEntriesRet = nodeServer.handleAppendEntries(appendEntriesReq);
                sendResult(ctx, appendEntriesRet);
            } else if (raftRequest.getRequestType() == RequestType.REQUEST_VOTE) {
                RequestVoteReq requestVoteReq = (RequestVoteReq) raftRequest.getRequest();
                RequestVoteRet requestVoteRet = nodeServer.handleRequestVote(requestVoteReq);
                sendResult(ctx, requestVoteRet);
            } else if (raftRequest.getRequestType() == RequestType.CLEINT_REQ) {
                ClientReq clientReq = (ClientReq) raftRequest.getRequest();
                ClientRet clientRet = nodeServer.handleClientRequest(clientReq);
                sendResult(ctx, clientRet);
            } else {
                sendFail(ctx, "Unsupported request type '" + raftRequest.getRequestType() + "'");
            }
        }
    }

    private void sendFail(ChannelHandlerContext ctx, String errorMsg) {
        RaftResponse raftResponse = RaftResponse.buildFail(errorMsg);
        writeResponse(ctx, RaftSerializeUtils.serialize(raftResponse));
    }

    private void sendResult(ChannelHandlerContext ctx, Object result) {
        RaftResponse raftResponse = RaftResponse.buildSuccess(result);
        writeResponse(ctx, RaftSerializeUtils.serialize(raftResponse));
    }

    private void writeResponse(ChannelHandlerContext ctx, byte[] bytes) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(bytes));
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().set(CONTENT_LENGTH, response.content().readableBytes());

        ctx.write(response);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
