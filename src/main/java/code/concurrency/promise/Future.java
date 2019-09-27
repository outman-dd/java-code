package code.concurrency.promise;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;

/**
 * 〈Future〉<p>
 *                                      +---------------------------+
 *                                      | Completed successfully    |
 *                                      +---------------------------+
 *                                 +---->      isDone() = true      |
 * +--------------------------+    |    |        result = non-null  |
 * |        Uncompleted       |    |    +===========================+
 * +--------------------------+    |    | Completed with failure    |
 * |      isDone() = false    |    |    +---------------------------+
 * |        result = null     |----+---->      isDone() = true      |
 * | isCancelled() = false    |    |    |       cause() = non-null  |
 * |       cause() = null     |    |    +===========================+
 * +--------------------------+    |    | Completed by cancellation |
 *                                 |    +---------------------------+
 *                                 +---->      isDone() = true      |
 *                                      | isCancelled() = true      |
 *                                      +---------------------------+
 *
 * @author zixiao
 * @date 2019/6/11
 */
public interface Future <R> {

    boolean isDone();

    boolean isSuccess();

    boolean isCancelled();

    boolean cancel();

    /**
     * 阻塞直到取得异步操作结果
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     */
    R get() throws InterruptedException, ExecutionException;

    /**
     * 阻塞直到超时或取得异步操作结果
     * @param timeout
     * @param unit
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    R get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * 完成回调
     * @param callback
     * @return
     */
    Future<R> whenComplete(BiConsumer<? super R, ? super Throwable> callback);

    /**
     * 设置完成
     */
    void setDone();

}
