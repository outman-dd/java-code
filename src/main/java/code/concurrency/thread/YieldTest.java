package code.concurrency.thread;

/**
 * 〈一句话功能简述〉<p>
 * 〈Thread.yield()方法 线程让步，就是说当一个线程使用了这个方法之后，它就会把自己CPU执行的时间让掉，使当前线程从Running变为Ready，让自己或者其它的线程运行。
 * cpu调度器会从众多的可执行态里选择，也就是说，当前也就是刚刚的那个线程还是有可能会被再次执行到的，并不是说一定会执行其他线程而该线程在下一次中不会执行到了。
 *
 * @author zixiao
 * @date 2019/10/9
 */
public class YieldTest extends Thread {

    public YieldTest(String name) {
        super(name);
    }

    @Override
    public void run() {
        for (int i = 1; i <= 10; i++) {
            System.out.println("" + this.getName() + "-----" + i);
            // 该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
            if (i == 8) {
                this.yield();
            }
        }
    }

    public static void main(String[] args) {
        YieldTest yt1 = new YieldTest("张三");
        YieldTest yt2 = new YieldTest("李四");
        YieldTest yt3 = new YieldTest("王五");
        YieldTest yt4 = new YieldTest("赵六");
        YieldTest yt5 = new YieldTest("007");

        yt1.start();
        yt2.start();
        yt3.start();
        yt4.start();
        yt5.start();
    }

}