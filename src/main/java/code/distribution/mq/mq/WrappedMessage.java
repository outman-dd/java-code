package code.distribution.mq.mq;

import lombok.Data;

import java.io.Serializable;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/26
 */
@Data
public class WrappedMessage implements Serializable{

    private String bizKey;

    private String topic;

    private Object param;

    private long createTimestamp;

    public WrappedMessage(String bizKey, String topic, Object param) {
        this.bizKey = bizKey;
        this.topic = topic;
        this.param = param;
        this.createTimestamp = System.currentTimeMillis();
    }
}
