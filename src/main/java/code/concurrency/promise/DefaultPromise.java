package code.concurrency.promise;

/**
 * 〈DefaultPromise〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/11
 */
public class DefaultPromise<R> extends AbstractFuture<R> implements Promise<R> {

    @Override
    public Future<R> getFuture() {
        return this;
    }

    @Override
    public Promise<R> setSuccess(R result) throws IllegalStateException {
        if (trySuccess(result)) {
            return this;
        }
        throw new IllegalStateException("Set success exception.");
    }

    @Override
    public boolean trySuccess(R result) {
        if (result == null) {
            throw new NullPointerException("Result can not be null");
        }

        if (isDone()) {
            return false;
        }

        doneLock.lock();
        try {
            if (!isDone()) {
                this.result = result;
                setDone();
                return true;
            }
        } finally {
            doneLock.unlock();
        }
        return false;
    }

    @Override
    public Promise<R> setFailure(Throwable cause) {
        if (tryFailure(cause)) {
            return this;
        }
        throw new IllegalStateException("Set failure exception.");
    }

    @Override
    public boolean tryFailure(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("Cause can not be null");
        }

        if (isDone()) {
            return false;
        }

        doneLock.lock();
        try {
            if (!isDone()) {
                this.cause = cause;
                setDone();
                return true;
            }
        } finally {
            doneLock.unlock();
        }
        return false;
    }

}
