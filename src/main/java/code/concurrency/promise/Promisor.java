package code.concurrency.promise;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 〈Promisor〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/3
 */
public interface Promisor {


    /**
     * 启动异步任务的执行
     * @param callable
     * @param <R>
     * @return
     */
    <R> Future<R> asyncExecute(Callable<R> callable);

    /**
     * 同步执行任务
     * @param callable
     * @param <R>
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    <R> R execute(Callable<R> callable) throws ExecutionException, InterruptedException;

    /**
     * 停止
     * @param time
     * @param unit
     */
    void shutdown(long time, TimeUnit unit);

}
