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
public class P1114 {

    private Lock lock = new ReentrantLock();

    private Condition codi2 = lock.newCondition();
    private Condition codi3 = lock.newCondition();

    private int i = 0;

    public void test1(){
        lock.lock();
        try {
            System.out.println("first");
            i++;
            codi2.signal();
        } finally {
            lock.unlock();
        }
    }

    public void test2(){
        lock.lock();
        try {
            if(i!=1){
                codi2.await();
            }
            i++;
            System.out.println("second");
            codi3.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void test3(){
        lock.lock();
        try {
            if(i!=2){
                codi3.await();
            }
            System.out.println("third");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
