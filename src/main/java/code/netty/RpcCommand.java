package code.netty;

import code.serialize.SerializeType;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * 〈Rpc命令〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/4/4
 */
public class RpcCommand implements Serializable {

    /**
     * 序列化方式
     */
    private SerializeType serializeType = SerializeType.Hessian;

    /**
     * 数据body
     */
    private byte[] body;

    public RpcCommand(byte[] body) {
        this.body = body;
    }

    public RpcCommand(SerializeType serializeType, byte[] body) {
        this.serializeType = serializeType;
        this.body = body;
    }

    public static RpcCommand decode(final ByteBuffer byteBuffer) {
        //总长度
        int length = byteBuffer.limit();

        //协议类型 1byte
        byte protocolType = byteBuffer.get();

        //body长度
        int bodyLength = length - Protocol.PROTOCOL_TYPE_LENGTH;
        byte[] bodyData = null;
        if (bodyLength > 0) {
            bodyData = new byte[bodyLength];
            byteBuffer.get(bodyData);
        }

        return new RpcCommand(SerializeType.valueOf(protocolType), bodyData);
    }

    public ByteBuffer encode() {
        /******* 计算数据长度 *******/
        // 1> protocol type size
        int length = Protocol.PROTOCOL_TYPE_LENGTH;

        // 2> body data length
        if (this.body != null) {
            length += body.length;
        }

        /******* 写入ByteBuffer *******/
        //分配空间
        ByteBuffer result = ByteBuffer.allocate(Protocol.LENGTH_FILED_LENGTH + length);

        // 1、length
        result.putInt(length);

        // 2、protocol type
        result.put(serializeType.getCode());

        // 3、body data;
        if (this.body != null) {
            result.put(this.body);
        }

        result.flip();

        return result;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public void setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
