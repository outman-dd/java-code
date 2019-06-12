package code.concurrency.promise;

import java.util.concurrent.*;

/**
 * 〈DefaultPromisor〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/6/3
 */
public class DefaultPromisor implements Promisor {

    private ExecutorService executorService;

    public DefaultPromisor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * 启动异步任务的执行，并返回用于获取异步任务执行结果的凭据对象
     */
    @Override
    public <R> Future<R> asyncExecute(Callable<R> callable){
        if(callable == null){
            throw new NullPointerException("Callable can not be null");
        }
        Promise<R> promise = new DefaultPromise<>();
        try {
            executorService.execute(new PromiseTask<>(promise, callable));
        } catch (RejectedExecutionException e) {
            promise.setFailure(e);
        }
        return promise.getFuture();
    }

    @Override
    public <R> R execute(Callable<R> callable) throws ExecutionException, InterruptedException {
        Future<R> future = asyncExecute(callable);
        return future.get();
    }

    private static class PromiseTask<R> implements Runnable{

        private Promise<R> promise;

        private Callable<R> callable;

        public PromiseTask(Promise<R> promise, Callable<R> callable) {
            this.promise = promise;
            this.callable = callable;
        }

        @Override
        public void run(){
            try {
                R r = callable.call();
                promise.trySuccess(r);
            } catch (Exception e) {
                promise.tryFailure(e);
            }
        }
    }

    @Override
    public void shutdown(long time, TimeUnit unit){
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
