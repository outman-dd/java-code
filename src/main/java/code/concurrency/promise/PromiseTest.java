package code.concurrency.promise;

import code.util.ThreadPool;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/12
 */
public class PromiseTest {

    private ExecutorService executorService = ThreadPool.create(100, 100, 1024);

    private Promisor promisor = new DefaultPromisor(executorService);

    @Test
    public void testAsync(){
        for (int i = 0; i < 100; i++) {
            promisor.asyncExecute(new Task(i)).whenComplete((s, t) -> {
                if (t != null) {
                    System.out.println("Completed fail: " + t.getMessage());
                } else {
                    System.out.println("Completed success: " + s);
                }
            });
        }
        promisor.shutdown(10, TimeUnit.SECONDS);
    }

    @Test
    public void testSync() throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            try {
                String s = promisor.execute(new Task(i));
                System.out.println("Completed success: " + s);
            } catch (ExecutionException e) {
                System.out.println("Completed fail: " + e.getMessage());
            }
        }
        promisor.shutdown(0, TimeUnit.SECONDS);
    }

    private class Task implements Callable<String> {

        private int param;

        public Task(int param) {
            this.param = param;
        }

        @Override
        public String call() throws Exception {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
            int ret = 1000 / (param % 10);
            return MessageFormat.format("1000/({0}%10)={1}", param, ret);
        }
    }

}
