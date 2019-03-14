package code.concurrency.lock;

import code.concurrency.lock.aqs.AbstractQueuedSync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * 〈非阻塞锁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/24
 */
public class NonBlockingLock implements ILock {

    private final Sync sync;

    public NonBlockingLock() {
        this.sync = new Sync();
    }

    private static class Sync extends AbstractQueuedSync{

        /**
         * Attempts to acquire in exclusive mode. This method should query
         * if the state of the object permits it to be acquired in the
         * exclusive mode, and if so to acquire it.
         *
         * <p>This method is always invoked by the thread performing
         * acquire.  If this method reports failure, the acquire method
         * may queue the thread, if it is not already queued, until it is
         * signalled by a release from some other thread. This can be used
         * to implement method {@link ILock#tryLock()}.
         *
         * <p>The default
         * implementation throws {@link UnsupportedOperationException}.
         *
         * @param arg the acquire argument. This value is always the one
         *        passed to an acquire method, or is the value saved on entry
         *        to a condition wait.  The value is otherwise uninterpreted
         *        and can represent anything you like.
         * @return {@code true} if successful. Upon success, this object has
         *         been acquired.
         * @throws IllegalMonitorStateException if acquiring would place this
         *         synchronizer in an illegal state. This exception must be
         *         thrown in a consistent fashion for synchronization to work
         *         correctly.
         * @throws UnsupportedOperationException if exclusive mode is not supported
         */
        @Override
        protected boolean tryAcquire(int arg) {
            Thread current = Thread.currentThread();
            int currentState = getState();
            if(currentState == 0){
                //有线程在前面排队，直接放弃拿锁
                if(hasQueuedPredecessors()){
                    return false;
                }
                boolean flag = compareAndSetState(0, arg);
                if(flag){
                    setExclusiveOwnerThread(current);
                }
                return flag;
            //可重入
            }else if(current == getExclusiveOwnerThread()){
                int newState = currentState + arg;
                if(newState < 0){
                    //int溢出
                    throw new IllegalMonitorStateException("Reach max lock limit");
                }
                setState(newState);
                return true;
            }
            return false;
        }

        /**
         * Attempts to set the state to reflect a release in exclusive
         * mode.
         *
         * <p>This method is always invoked by the thread performing release.
         *
         * <p>The default implementation throws
         * {@link UnsupportedOperationException}.
         *
         * @param arg the release argument. This value is always the one
         *        passed to a release method, or the current state value upon
         *        entry to a condition wait.  The value is otherwise
         *        uninterpreted and can represent anything you like.
         * @return {@code true} if this object is now in a fully released
         *         state, so that any waiting threads may attempt to acquire;
         *         and {@code false} otherwise.
         * @throws IllegalMonitorStateException if releasing would place this
         *         synchronizer in an illegal state. This exception must be
         *         thrown in a consistent fashion for synchronization to work
         *         correctly.
         * @throws UnsupportedOperationException if exclusive mode is not supported
         */
        @Override
        protected boolean tryRelease(int arg) {
            Thread current = Thread.currentThread();
            if(current != getExclusiveOwnerThread()){
                throw new IllegalMonitorStateException("Thread has not own the lock");
            }
            int state = getState() - arg;
            boolean flag = false;
            if(state == 0){
                flag = true;
                setExclusiveOwnerThread(null);
            }
            setState(state);
            return flag;
        }

    }

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock(){
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
       throw new UnsupportedOperationException();
    }

}
