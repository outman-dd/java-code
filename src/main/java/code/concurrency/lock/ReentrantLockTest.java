package code.concurrency.lock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * 〈可重入锁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/14
 */
public class ReentrantLockTest{

    private final ILock nonfairLock = new ReentrantLock();

    private final ILock fairLock = new ReentrantLock(true);

    private final Lock jdkLock = new java.util.concurrent.locks.ReentrantLock();

    private final int forNum = 1;

    private final int threads = 8;
    @Test
    public void lockTest() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        for(int i=0; i<threads; i++){
            threadList.add(new Thread(new Task(nonfairLock)));
        }

        long start = System.currentTimeMillis();
        for (Thread thread : threadList) {
            thread.start();
        }
        threadList.get(threads-1).join();

        System.out.println("nonfairLock cost:"+(System.currentTimeMillis()-start)+"ms");
    }

    @Test
    public void jdkLockTest() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        for(int i=0; i<threads; i++){
            threadList.add(new Thread(new Task(jdkLock)));
        }

        long start = System.currentTimeMillis();
        for (Thread thread : threadList) {
            thread.start();
        }
        threadList.get(threads-1).join();

        System.out.println("nonfairLock cost:"+(System.currentTimeMillis()-start)+"ms");
    }

    @Test
    public void fairLockTest() throws InterruptedException {
        List<Thread> threadList = new ArrayList<>();
        for(int i=0; i<threads; i++){
            threadList.add(new Thread(new Task(fairLock)));
        }

        long start = System.currentTimeMillis();
        for (Thread thread : threadList) {
            thread.start();
        }
        threadList.get(threads-1).join();

        System.out.println("fairLock cost:" + (System.currentTimeMillis() - start) + "ms");
    }

    class Task implements Runnable{

        private Lock lock;

        private String msg = ">>>";

        public Task(Lock lock){
            this.lock = lock;
        }

        @Override
        public void run() {
            for(int i=0; i<forNum; i++){
                print(i);
            }
        }

        private void print(int idx){
            lock.lock();
            try {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //
                }
                System.out.println(Thread.currentThread().getName()+":"+ msg + idx);
            }finally {
                lock.unlock();
            }
        }
    }

}
