package code.algorithm.slidingwindow;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/24
 */
public class SlidingWindowTest {

    public static void main(String[] args) throws InterruptedException {
        int windowSize = 5;
        SlidingWindow slidingWindow = new SlidingWindow(60, windowSize);
        ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                SlidingNode node = slidingWindow.lastWindow();
                System.out.println(node.prettyPrint());
            }
        }, windowSize, 3, TimeUnit.SECONDS);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<2000; i++){
                    int rt = new Random().nextInt(21);
                    slidingWindow.current().addCount(1);
                    slidingWindow.current().addRt(rt);
                    try {
                        Thread.sleep(rt);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<2000; i++){
                    int rt = new Random().nextInt(21);
                    slidingWindow.current().addCount(1);
                    slidingWindow.current().addRt(rt);
                    try {
                        Thread.sleep(rt);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t2.setDaemon(true);
        t2.start();

        Thread.sleep(30000);
        scheduler.shutdownNow();
        slidingWindow.close();
    }
}
