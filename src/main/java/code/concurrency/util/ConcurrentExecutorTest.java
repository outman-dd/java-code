package code.concurrency.util;

import code.util.DateFormatUtils;
import org.junit.Test;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 〈并发测试〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/14
 */
public class ConcurrentExecutorTest {

    private int taskNum = 100;

    @Test
    public void testConcurrentExecutor() throws InterruptedException {
        ConcurrentExecutor concurrentExecutor = new ConcurrentExecutor(taskNum);
        for(int i=0; i<taskNum; i++){
            concurrentExecutor.execute(new Task());
        }

        Thread.sleep(100);
        concurrentExecutor.shutdown();
    }

    @Test
    public void testFor() throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(taskNum);
        for(int i=0; i<taskNum; i++){
            threadPool.execute(new Task());
        }

        Thread.sleep(100);
        threadPool.shutdown();
    }

    static class Task implements Runnable{

        @Override
        public void run() {
            String executeTime = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
            System.out.println("Task execute in '"+Thread.currentThread().getName() + "' at time: "+executeTime);
            try {
                Thread.sleep(new Random().nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
