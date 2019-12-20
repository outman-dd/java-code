package code.collection.disruptor;

import com.lmax.disruptor.RingBuffer;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/25
 */
public class LogEventProducer {

    private final RingBuffer<LogEvent> ringBuffer;

    public LogEventProducer(RingBuffer<LogEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void produce(String s) {
        long seq = ringBuffer.next();
        try {
            LogEvent event = ringBuffer.get(seq);
            event.setValue(s);
        } finally {
            ringBuffer.publish(seq);
        }
    }

}
