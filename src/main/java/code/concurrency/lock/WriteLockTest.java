package code.concurrency.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 〈可重入测试〉<p>
 * 〈读写锁最大可重入锁数 65535
 *  int型state，高16位读状态 低16位写状态〉
 *
 * -Xss10m, 防止StackOverflowError
 *
 * @author zixiao
 * @date 19/1/23
 */
public class WriteLockTest {

    public int lockedCount = 0;

    private int max = 100000;

    private Lock lock = new ReentrantReadWriteLock().writeLock();

    public void doNext() {
        if(lockedCount>=max){
            return;
        }
        lock.lock();
        lockedCount++;
        try {
            doNext();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        WriteLockTest writeLockTest = new WriteLockTest();
        try {
            writeLockTest.doNext();
            System.out.println("Lock count:" + writeLockTest.lockedCount);
        } catch (Throwable e) {
            System.err.println("Lock count:" + writeLockTest.lockedCount + ", " + e.getMessage());
            //throw e;
        }
    }


}
