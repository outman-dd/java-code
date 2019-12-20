package code.concurrency.thread;

import org.junit.Test;
import org.springframework.util.Assert;

import java.util.concurrent.locks.LockSupport;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/10/9
 */
public class ThreadState {
    
    /**
     * Object.wait() 线程状态 -> WAITING，让出锁
     * @throws InterruptedException
     */
    @Test
    public void waitLock() throws InterruptedException {
        Object lock = new Object();
        Thread threadA = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadA.start();
        Thread.sleep(5);
        System.out.println("thread A state: " + threadA.getState());
        Assert.isTrue(threadA.getState().equals(Thread.State.WAITING), "");

        Thread threadB = new Thread(() -> {
            synchronized (lock) {
                System.out.println("thread B do stm");
            }
        });
        threadB.start();
        Thread.sleep(5);
        System.out.println("thread B state: " + threadB.getState());   //TERMINATED
        Assert.isTrue(threadB.getState().equals(Thread.State.TERMINATED), "");
    }

    /**
     * Object.wait(long) 线程状态 -> TIMED_WAITING，让出锁
     * @throws InterruptedException
     */
    @Test
    public void waitTimeLock() throws InterruptedException {
        Object lock = new Object();
        Thread threadA = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadA.start();
        Thread.sleep(5);
        System.out.println("thread A state: " + threadA.getState());
        Assert.isTrue(threadA.getState().equals(Thread.State.TIMED_WAITING), "");

        Thread threadB = new Thread(() -> {
            synchronized (lock) {
                System.out.println("thread B do stm");
            }
        });
        threadB.start();
        Thread.sleep(5);
        System.out.println("thread B state: " + threadB.getState());
        Assert.isTrue(threadB.getState().equals(Thread.State.TERMINATED), "");
    }

    /**
     * Thread.sleep(long) 线程状态 -> TIMED_WAITING，不释放锁
     * @throws InterruptedException
     */
    @Test
    public void sleepLock() throws InterruptedException {
        Object lock = new Object();
        Thread threadA = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        threadA.start();
        Thread.sleep(5);
        System.out.println("thread A state: " + threadA.getState());
        Assert.isTrue(threadA.getState().equals(Thread.State.TIMED_WAITING), "");

        Thread threadB = new Thread(() -> {
            synchronized (lock) {
                System.out.println("thread B do stm");
            }
        });
        threadB.start();
        Thread.sleep(5);
        System.out.println("thread B state: " + threadB.getState());
        Assert.isTrue(threadB.getState().equals(Thread.State.BLOCKED), "");
    }

    /**
     * LockSupport.park() 线程状态 -> WAITING
     * @throws InterruptedException
     */
    @Test
    public void lockPark() throws InterruptedException {
        Thread threadA = new Thread(() -> {
            LockSupport.park(this);
        });
        threadA.start();
        Thread.sleep(5);
        System.out.println("thread A state: " + threadA.getState());
        Assert.isTrue(threadA.getState().equals(Thread.State.WAITING), "");

        LockSupport.unpark(threadA);
        Thread.sleep(5);
        System.out.println("Unpark, thread A state: " + threadA.getState());
        Assert.isTrue(threadA.getState().equals(Thread.State.TERMINATED), "");
    }

    /**
     * Thread.join() 线程状态 -> WAITING
     * @throws InterruptedException
     */
    @Test
    public void join() throws InterruptedException {
        Thread threadA = new Thread(() -> {
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < 50000) {
                //
            }
            System.out.println("thread A finished");
        });
        threadA.start();
        Thread.sleep(5);

        System.out.println("thread A state: " + threadA.getState());
        Assert.isTrue(threadA.getState().equals(Thread.State.RUNNABLE), "");

        Thread threadB = new Thread(() -> {
            try {
                threadA.join();
                System.out.println("thread B do stm");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        threadB.start();

        Thread.sleep(5);
        System.out.println("thread B state: " + threadB.getState());
        Assert.isTrue(threadB.getState().equals(Thread.State.WAITING), "");

        Thread.sleep(49999);
        System.out.println("thread B state: " + threadB.getState());
        Assert.isTrue(threadB.getState().equals(Thread.State.TERMINATED), "");
    }
}
