package code.concurrency.lock;

import code.concurrency.lock.aqs.AbstractQueuedSync;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * 〈可重入锁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/14
 */
public class ReentrantLock implements Lock{

    private final Sync sync;

    public ReentrantLock() {
        this.sync = new NonfairSync();
    }

    public ReentrantLock(boolean fair) {
        if(fair){
            this.sync = new FairSync();
        }else{
            this.sync = new NonfairSync();
        }
    }

    abstract class Sync extends AbstractQueuedSync {
        /**
         * Performs {@link java.util.concurrent.locks.Lock#lock}. The main reason for subclassing
         * is to allow fast path for nonfair version.
         */
        abstract void lock();

        protected boolean tryLock(){
            return tryAcquire(1);
        }
        
        @Override
        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;
            if (Thread.currentThread() != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException();
            }
            boolean free = false;
            if (c == 0) {
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);
            return free;
        }

        @Override
        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        final boolean isLocked() {
            return getState() != 0;
        }

    }

    private class FairSync extends Sync {

        @Override
        void lock() {
            acquire(1);
        }

        @Override
        protected boolean tryAcquire(int arg) {
            Thread current = Thread.currentThread();
            int currentState = getState();
            if(currentState == 0){
                //如果前面没有线程在排队，就尝试获取锁
                if(!hasQueuedPredecessors() && compareAndSetState(0, arg)){
                    setExclusiveOwnerThread(current);
                    return true;
                }
                //当前持有锁是自己线程，则继续拿锁，可重入性
            }else if(current == getExclusiveOwnerThread()){
                //以下为单线程操作
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
    }

    private class NonfairSync extends Sync{
        @Override
        void lock() {
            //一上来直接抢锁
            if (compareAndSetState(0, 1)) {
                //抢到直接拿锁，不进入AQS同步队列
                setExclusiveOwnerThread(Thread.currentThread());
            }else{
                acquire(1);
            }
        }

        @Override
        protected boolean tryAcquire(int arg) {
            Thread current = Thread.currentThread();
            int currentState = getState();
            if(currentState == 0){
                //不排队先抢锁，抢不到再去排队
                if(compareAndSetState(0, arg)){
                    setExclusiveOwnerThread(current);
                    return true;
                }
            //当前持有锁是自己线程，则继续拿锁，可重入性
            }else if(current == getExclusiveOwnerThread()){
                //以下为单线程操作
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
    }

    @Override
    public void lock() {
        sync.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryLock();
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
