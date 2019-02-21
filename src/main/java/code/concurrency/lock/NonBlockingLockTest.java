package code.concurrency.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 19/1/24
 */
public class NonBlockingLockTest {

    private NonBlockingLock lock = new NonBlockingLock();

    public static void main(String[] args) {
        NonBlockingLockTest test = new NonBlockingLockTest();
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 100; i++) {
            final int index = i;
            executorService.execute(() -> test.test(index));
        }
        executorService.shutdown();
    }

    private void test(int i){
        if(!lock.tryLock()){
            System.out.println(i+":"+Thread.currentThread().getName() + " cancel acquire lock");
            return;
        }
        try {
            System.out.println(i+":"+Thread.currentThread().getName() + " get lock");
            TimeUnit.NANOSECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

}
