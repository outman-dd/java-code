package code.concurrency.promise;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

/**
 * 〈AbstractFuture〉<p>
 *
 * @author zixiao
 * @date 2019/6/11
 */
public abstract class AbstractFuture<R> implements Future<R> {

    protected volatile R result;

    protected Throwable cause;

    protected boolean done = false;

    protected boolean cancelled = false;

    protected CountDownLatch latch = new CountDownLatch(1);

    protected Lock doneLock = new ReentrantLock();

    protected AtomicReference<Callback> callbackReference = new AtomicReference<>();

    @Override
    public boolean isDone() {
        return done;
    }

    @Override
    public boolean isSuccess() {
        return isDone() && result != null;
    }

    @Override
    public boolean isCancelled() {
        return isDone() && cancelled;
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException();
    }

    @Override
    public R get() throws InterruptedException, ExecutionException {
        if (!isDone()) {
            latch.await();
        }

        if (isSuccess()) {
            return result;
        } else if (isCancelled()) {
            throw new ExecutionException("Task has been cancelled", null);
        }

        throw new ExecutionException(cause);
    }

    @Override
    public R get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (!isDone()) {
            if (latch.await(timeout, unit) && !isDone()) {
                throw new TimeoutException("Get result timeout");
            }
        }

        if (isSuccess()) {
            return result;
        } else if (isCancelled()) {
            throw new ExecutionException("Task has been cancelled", null);
        }

        throw new ExecutionException(cause);
    }

    @Override
    public Future<R> whenComplete(BiConsumer<? super R, ? super Throwable> callback) {
        callbackReference.compareAndSet(null, () -> callback.accept(this.result, this.cause));

        // 执行时间极端或线程池满的情况下，放入callback前已经完成，此处需要主动执行回调
        if (isDone()) {
            executeOnlyOnce();
        }
        return this;
    }

    @Override
    public void setDone() {
        this.done = true;
        latch.countDown();

        // 精确执行一次回调
        executeOnlyOnce();
    }

    private void executeOnlyOnce() {
        Callback callback = callbackReference.getAndSet(null);
        if (callback != null) {
            callback.call();
        }
    }

}
