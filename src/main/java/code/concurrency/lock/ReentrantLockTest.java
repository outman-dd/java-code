package code.concurrency.lock;

import org.junit.Test;

/**
 * 〈可重入锁〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/14
 */
public class ReentrantLockTest{

    private final Lock nonfairLock = new ReentrantLock(false);

    private final Lock fairLock = new ReentrantLock(true);

    @Test
    public void nonfairLockTest() throws InterruptedException {
        Thread t1 = new Thread(new Task(nonfairLock, "AAA"));
        Thread t2 = new Thread(new Task(nonfairLock, "BBB"));

        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        t2.join();
        System.out.println("nonfairLock cost:"+(System.currentTimeMillis()-start)+"ms");
    }

    @Test
    public void fairLockTest() throws InterruptedException {
        Thread t1 = new Thread(new Task(fairLock, "AAA"));
        Thread t2 = new Thread(new Task(fairLock, "BBB"));

        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        t2.join();
        System.out.println("fairLock cost:" + (System.currentTimeMillis() - start) + "ms");
    }


    class Task implements Runnable{

        private Lock lock;

        private String msg;

        public Task(Lock lock, String msg){
            this.lock = lock;
            this.msg = msg;
        }

        @Override
        public void run() {
            for(int i=0; i<100; i++){
                print(i);
            }

        }

        private void print(int idx){
            lock.lock();
            try {
                try {
                    Thread.sleep(idx%3);
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
