package code.concurrency.promise;

/**
 * 〈Promise〉<p>
 *
 * @author zixiao
 * @date 2019/6/3
 */
public interface Promise<R> extends Future<R>{

    /**
     * 获取只读的future
     * @return
     */
    Future<R> getFuture();

    /**
     * 标记异步操作结果为成功
     * 如果已被设置（不管成功还是失败）,则抛出异常IllegalStateException
     *
     * @param result
     * @return
     * @throws IllegalStateException
     */
    Promise<R> setSuccess(R result) throws IllegalStateException;

    /**
     * 标记异步操作结果为成功
     * 如果已被设置（不管成功还是失败）,则返回false
     *
     * @param result
     * @return 是否设置成功
     */
    boolean trySuccess(R result);

    /**
     * 标记异步操作结果为失败
     * 如果已被设置（不管成功还是失败）,则抛出异常IllegalStateException
     *
     * @param cause
     * @return
     * @throws IllegalStateException
     */
    Promise<R> setFailure(Throwable cause);

    /**
     * 记异步操作结果为失败
     * 如果已被设置（不管成功还是失败）,则返回false
     *
     * @param cause
     * @return 是否设置成功
     */
    boolean tryFailure(Throwable cause);


}
