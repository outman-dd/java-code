package code.concurrency.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 〈并发执行器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/14
 */
public class ConcurrentExecutor implements ExecutorService{

    /**
     * 并发数
     */
    private int concurrentNum;

    /**
     * 线程池
     */
    private ExecutorService threadPool;

    /**
     * 循环栅栏
     */
    private CyclicBarrier cyclicBarrier;

    public ConcurrentExecutor(int concurrentNum) {
        this(concurrentNum, Executors.newCachedThreadPool());
    }

    public ConcurrentExecutor(int concurrentNum, ExecutorService threadPool) {
        if (concurrentNum <= 0) {
            throw new IllegalArgumentException("concurrentNum > 0");
        }
        if (threadPool == null){
            throw new IllegalArgumentException("threadPool not null");
        }
        this.concurrentNum = concurrentNum;
        this.threadPool = threadPool;
        this.cyclicBarrier = new CyclicBarrier(this.concurrentNum);
    }

    @Override
    public void execute(Runnable task){
        threadPool.execute(new ConcurrentRunnable(task, cyclicBarrier));
    }


    @Override
    public void shutdown() {
        threadPool.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return threadPool.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return threadPool.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return threadPool.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return threadPool.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task){
        return threadPool.submit(new ConcurrentCallable(task, cyclicBarrier));
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return submit(new ConcurrentRunnable(task, cyclicBarrier), result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return threadPool.submit(new ConcurrentRunnable(task, cyclicBarrier));
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return threadPool.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return threadPool.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return threadPool.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return threadPool.invokeAny(tasks, timeout, unit);
    }


}
