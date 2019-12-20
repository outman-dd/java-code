package code.biz.delayqueue;

import code.util.ThreadPool;
import code.util.WheelTask;
import code.util.WheelTimer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/9/9
 */
public class DelayQueue extends WheelTimer<DelayQueue.DelayTask> {

    private ExecutorService executorService = ThreadPool.create(4, 4, 1024);

    @Override
    protected void doTask(WheelTask<DelayTask> task) {
        executorService.execute(task.getTask());
    }

    public static void main(String[] args) throws InterruptedException {
        DelayQueue delayQueue = new DelayQueue();
        delayQueue.start();
        System.out.println("Start at " + LocalTime.now());
        for(int i=0; i<200; i++){
            final String bizNo = String.valueOf(System.nanoTime());
            delayQueue.addTask(new DelayTask(bizNo) {
                @Override
                protected void run(String bizNo) {
                    System.out.println(LocalTime.now() + " - " + bizNo);
                }
            }, 1 + new Random().nextInt(30));
        }
        delayQueue.executorService.awaitTermination(1, TimeUnit.MINUTES);
        System.out.println("Stop at " + LocalTime.now());
    }

    @Data
    public abstract static class DelayTask implements Runnable{

        private String bizNo;

        public DelayTask(String bizNo) {
            this.bizNo = bizNo;
        }

        @Override
        public void run() {
            run(bizNo);
        }

        protected abstract void run(String bizNo);
    }
}
