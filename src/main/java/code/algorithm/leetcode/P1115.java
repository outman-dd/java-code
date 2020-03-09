package code.algorithm.leetcode;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/2/17
 */
public class P1115 {

    private int n;

    private Lock lock = new ReentrantLock();

    private Condition codi = lock.newCondition();

    private volatile boolean flag = true;

    public P1115(int n) {
        this.n = n;
    }

    public void foo(Runnable printFoo) throws InterruptedException {

        for (int i = 0; i < n; i++) {
            lock.lock();
            try{
                if(!flag){
                    codi.await();
                }
                printFoo.run();
                flag = false;
                codi.signal();
            }finally {
                lock.unlock();
            }
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {

        for (int i = 0; i < n; i++) {
            lock.lock();
            try{
                if(flag){
                    codi.await();
                }
                printBar.run();
                flag = true;
                codi.signal();
            }finally {
                lock.unlock();
            }
        }
    }

}
