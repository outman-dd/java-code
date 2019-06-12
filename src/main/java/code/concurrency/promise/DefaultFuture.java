package code.concurrency.promise;

import java.util.function.BiConsumer;

/**
 * 〈DefaultFuture〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/12
 */
public class DefaultFuture<R> extends AbstractFuture<R>{

    @Override
    public Future<R> whenComplete(BiConsumer<? super R, ? super Throwable> callback){
        callbackReference.compareAndSet(null, new DefaultCallback(callback, this));
        if(isDone()){
            executeCallbackOnce();
        }
        return this;
    }

    private class DefaultCallback implements Callback {

        private BiConsumer<? super R, ? super Throwable> callback;

        private AbstractFuture<R> future;

        public DefaultCallback(BiConsumer<? super R, ? super Throwable> callback, AbstractFuture<R> future) {
            this.callback = callback;
            this.future = future;
        }

        public void call(){
            callback.accept(future.result, future.cause);
        }
    }

}
