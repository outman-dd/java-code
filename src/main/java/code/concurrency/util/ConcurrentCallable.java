package code.concurrency.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;

/**
 * 〈并发Callable〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/14
 */
public class ConcurrentCallable<T> implements Callable{

    private Callable<T> callable;

    private CyclicBarrier cyclicBarrier;

    public ConcurrentCallable(Callable<T> callable, CyclicBarrier cyclicBarrier) {
        this.callable = callable;
        this.cyclicBarrier = cyclicBarrier;
    }

    @Override
    public T call() throws Exception {
        //等待所有线程准备就绪
        cyclicBarrier.await();
        return callable.call();
    }
}
