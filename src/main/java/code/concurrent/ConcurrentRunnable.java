package code.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * 〈并发Runnable〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/14
 */
public class ConcurrentRunnable implements Runnable{

    private Runnable runnable;

    private CyclicBarrier cyclicBarrier;

    public ConcurrentRunnable(Runnable runnable, CyclicBarrier cyclicBarrier) {
        this.runnable = runnable;
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public void run() {
        try {
            //等待所有线程准备就绪
            cyclicBarrier.await();
            runnable.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

}
