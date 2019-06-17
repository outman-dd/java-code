package code.concurrency.util;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 *〈并发模拟测试〉<p>
 *
 * 实现并发的2种方式：
 * 1、倒计时计数器
 * 2、循环栅栏
 *
 * @author zixiao
 * @date 2016/4/28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ConcurrentTest {

    /**
     * 并发任务数
     */
    private static final int TASK_NUM = 500;

    /**
     * 原子变量，线程并发处理
     */
    @Test
    public void testAtomic() throws InterruptedException {
        AtomicInteger atomicCount = new AtomicInteger(TASK_NUM);
        ExecutorService executor = Executors.newFixedThreadPool(100);

        for(int i=0; i<TASK_NUM; i++){
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new AtomicTask(atomicCount), executor);
            future.whenComplete((r, t) ->{
                if(t != null){
                    t.printStackTrace();
                }else{
                    System.out.println("Task result:"+ r);
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * 倒计时计数器，实现并发
     */
    @Test
    public void testCountDownLatch(){
        int count = 10;
        //10名选手
        CountDownLatch ready = new CountDownLatch(count);
        //1名裁判
        CountDownLatch start = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(count);
        for(int i=0;i<TASK_NUM;i++){
            executor.submit(new CountDownLatchTask(ready, start));
        }

        System.out.println("裁判：等运动员就位。。。");
        try {
            ready.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("裁判：预备，开始！");
        start.countDown();

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 循环栅栏，实现并发
     */
    @Test
    public void testCyclicBarrier(){
        CyclicBarrier cyclicBarrier = new CyclicBarrier(TASK_NUM);
        ExecutorService executor = Executors.newFixedThreadPool(TASK_NUM);
        for(int i=0;i<TASK_NUM;i++){
            executor.execute(new CyclicBarrierTask(cyclicBarrier));
        }

        executor.shutdown();

        while (!executor.isTerminated()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

/**
 * 原子变量，线程安全
 */
class AtomicTask implements Supplier<Integer>{

    private AtomicInteger atomicCount;

    public AtomicTask(AtomicInteger atomicCount){
        this.atomicCount = atomicCount;
    }

    @Override
    public Integer get() {
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return atomicCount.decrementAndGet();
    }
}

/**
 * 倒计时计数器
 */
class CountDownLatchTask implements Runnable{
    private CountDownLatch ready;

    private CountDownLatch start;

    public CountDownLatchTask(CountDownLatch ready, CountDownLatch start) {
        this.ready = ready;
        this.start = start;
    }

    public void run(){
        try {
            //1、准备
            Random random = new Random();
            System.out.println(Thread.currentThread().getName()+"：正在准备。。。");
            Thread.sleep(random.nextInt(300));

            //2、释放latch
            ready.countDown();
            System.out.println(Thread.currentThread().getName()+"：准备完毕,等裁判发令。。。");
            start.await();

            //3、继续执行
            System.out.println(Thread.currentThread().getName() + "：开跑。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}


/**
 * 循环栅栏
 */
class CyclicBarrierTask implements Runnable{

    private CyclicBarrier cyclicBarrier;

    public CyclicBarrierTask(CyclicBarrier cyclicBarrier) {
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        try {
            //1、准备数据
            Random random = new Random();
            System.out.println(Thread.currentThread().getName()+"：正在准备数据。。。");
            Thread.sleep(random.nextInt(300));

            //2、等待其他线程到这个位置
            System.out.println(Thread.currentThread().getName()+"：准备完毕，等待其他"+cyclicBarrier.getNumberWaiting()+"个小伙伴。。。");
            cyclicBarrier.await();

            //3、继续执行
            System.out.println(Thread.currentThread().getName() + "：人都到齐了，开始工作。。。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
