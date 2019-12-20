package code.collection.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/25
 */
public class LogEventTest {

    private static int THREADS = 64;

    private static int PER_NUM = 1024*1024;

    private static int TOTAL_NUM = THREADS*PER_NUM;

    private static int CAPACITY = 4096;

    private Disruptor<LogEvent> disruptor;

    private LogEventProducer producer;

    private AtomicInteger count = new AtomicInteger(0);

    @Before
    public void before(){
        // The factory for the event
        LogEventFactory factory = new LogEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = CAPACITY;

        // Construct the Disruptor
        disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);

        // Connect the handler
        disruptor.handleEventsWith(new LogEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LogEvent> ringBuffer = disruptor.getRingBuffer();

        this.producer = new LogEventProducer(ringBuffer);
    }

    @Test
    public void testDisruptor() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int t = 0; t < THREADS; t++) {
            Thread thread = new Thread(()->{
                for(int i=0; i<PER_NUM; i++){
                    producer.produce("Test");
                }
            });
            thread.start();
        }
        while (count.get() != TOTAL_NUM){}
        stopWatch.stop();

        System.out.println("Disruptor Count "+ count.get() +", cost "+ stopWatch.getTime() + "ms");
    }

    @Test
    public void testBlockingQueue(){
        BlockingQueue<LogEvent> queue = new ArrayBlockingQueue<LogEvent>(CAPACITY);
        Thread consumer = new Thread(()->{
            while (true){
                try {
                    LogEvent event = queue.take();
                    count.addAndGet(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        consumer.start();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int t = 0; t < THREADS; t++) {
            Thread thread = new Thread(()->{
                for(int i=0; i<PER_NUM; i++){
                    LogEvent event = new LogEvent();
                    event.setValue("Test");
                    try {
                        queue.put(event);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        while (count.get() != TOTAL_NUM){}
        stopWatch.stop();

        System.out.println("BlockingQueue Count "+ count.get() +", cost "+ stopWatch.getTime() + "ms");
    }

    @After
    public void after(){
        disruptor.shutdown();
    }

    class LogEventHandler implements EventHandler<LogEvent> {

        @Override
        public void onEvent(LogEvent event, long sequence, boolean endOfBatch) throws Exception {
            //System.out.println("Event:" + event.getValue() + ", seq:" + sequence);
            count.addAndGet(1);
        }
    }
}
