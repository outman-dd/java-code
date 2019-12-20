package code.collection.disruptor;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/25
 */
public class LogEventProducerWithTranslator {

    private static final EventTranslatorOneArg<LogEvent, String> TRANSLATOR =
            (event, sequence, s) -> event.setValue(s);

    private final RingBuffer<LogEvent> ringBuffer;

    public LogEventProducerWithTranslator(RingBuffer<LogEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void produce(String s) {
        ringBuffer.publishEvent(TRANSLATOR, s);
    }

}
