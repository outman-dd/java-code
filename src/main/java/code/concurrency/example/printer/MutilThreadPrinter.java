package code.concurrency.example.printer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈多线程打印 Condition〉<p>
 * 〈有三个线程分别打印A、B、C，请用多线程编程实现，在屏幕打印10次ABC能详细描述〉
 *
 * @author zixiao
 * @date 18/7/23
 */
public class MutilThreadPrinter {

    private static Lock lock = new ReentrantLock();

    private static Condition A = lock.newCondition();

    private static Condition B = lock.newCondition();

    private static Condition C = lock.newCondition();

    private static int count = 0;

    private static int PRINT_TIMES = 1000000;

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();
        new ThreadA().start();
        new ThreadB().start();
        Thread t3 = new ThreadC();
        t3.start();
        t3.join();
        System.out.println("\n\rCost:"+(System.currentTimeMillis()-start));
    }

    private static void print(String s){
        System.out.print(s);
        count++;
    }

    static class ThreadA extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                for (int i = 0; i < PRINT_TIMES; i++) {
                    if(count % 3 != 0){
                        A.await();//The lock associated with A is atomically released
                    }
                    print("A");
                    B.signal(); // A执行完唤醒B线程
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    static class ThreadB extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                for (int i = 0; i < PRINT_TIMES; i++) {
                    if(count % 3 != 1){
                        B.await();
                    }
                    print("B");
                    C.signal();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }

    static class ThreadC extends Thread {
        @Override
        public void run() {
            lock.lock();
            try {
                for (int i = 0; i < PRINT_TIMES; i++) {
                    if(count % 3 != 2){
                        C.await();
                    }
                    print("C");
                    A.signal();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }
    }
}
