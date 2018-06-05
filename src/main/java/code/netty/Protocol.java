package code.netty;

/**
 * 协议格式 <p>
 * 协议包格式
 * <length> <protocol type>  <body data>
 *  1 int   1 byte
 *
 * @author zixiao
 * @date 16/9/28
 */
public interface Protocol {

    /**
     * 数据总长度为int 占用4个字节
     */
    int LENGTH_FILED_LENGTH = 4;

    /**
     * 协议类型为byte 占用1个字节
     */
    int PROTOCOL_TYPE_LENGTH = 1;

}