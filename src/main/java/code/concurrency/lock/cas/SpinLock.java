package code.concurrency.lock.cas;

import code.concurrency.lock.Lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

/**
 * 〈自旋锁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/13
 */
public class SpinLock implements Lock{

    private AtomicReference<Thread> lock = new AtomicReference<>();

    @Override
    public void lock(){
        Thread t = Thread.currentThread();
        while(!lock.compareAndSet(null, t)){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                //
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        Thread t = Thread.currentThread();
        while(!lock.compareAndSet(null, t)){
            Thread.sleep(10);
        }
    }

    @Override
    public boolean tryLock() {
        Thread t = Thread.currentThread();
        return lock.compareAndSet(null, t);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock(){
        Thread t = Thread.currentThread();
        lock.compareAndSet(t, null);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
