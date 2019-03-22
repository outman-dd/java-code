package code.ratelimiter.algorithm;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 〈漏水桶〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/3/21
 */
public class LeakyBucket {

    private final BlockingQueue queue;

    private final ScheduledExecutorService scheduler;

    private final Object token = new Object();

    private final int MILLS = 1000;

    /**
     * 时间轮
     * 每个slot存放 需要漏出的数量
     */
    private final int[] timeWheel;

    /**
     * slot数
     */
    private int slotNum = 50;

    /**
     * 当前slot位置
     */
    private int currentSlot = 0;

    private LeakyBucket(int limitNum){
        queue = new LinkedBlockingQueue(limitNum);
        timeWheel = new int[slotNum];
        scheduler = new ScheduledThreadPoolExecutor(1);
    }

    /**
     * 初始化方法
     * @param limitNum
     */
    private void init(int limitNum){
        int times = limitNum/slotNum;
        int remainder = limitNum%slotNum;

        for(int i=0; i<slotNum; i++){
            timeWheel[i] = times;
        }

        if(remainder > 0){
            for(int j=0; j<remainder; j++){
                timeWheel[j] = timeWheel[j] + 1;
            }
        }

        int period = MILLS/slotNum;
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (currentSlot >= slotNum) {
                    currentSlot = 0;
                }
                int offerNum = timeWheel[currentSlot++];
                for (int i = 0; i < offerNum; i++) {
                    queue.poll();
                }
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    public static LeakyBucket create(int limitNum){
        if(limitNum <= 0){
            throw new IllegalArgumentException("The limitNum must be greater than 0 ");
        }
        LeakyBucket leakyBucket = new LeakyBucket(limitNum);
        leakyBucket.init(limitNum);
        return leakyBucket;
    }

    /**
     * 放入token，立即返回
     * @return 是否成功
     */
    public boolean put(){
        return queue.offer(token);
    }

    /**
     * 放入token，等待timeout返回
     * @param timeout 等待时间
     * @param unit
     * @return 是否成功
     * @throws InterruptedException
     */
    public boolean put(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.offer(token, timeout, unit);
    }

    /**
     * 放入token，如果桶已满，则阻塞等待
     * @return 是否成功
     * @throws InterruptedException
     */
    public boolean putWithBlocking() throws InterruptedException {
        queue.put(token);
        return true;
    }

    public void close(){
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        LeakyBucket leakyBucket = LeakyBucket.create(100);

        for(int i=0; i<500; i++){
            boolean success = leakyBucket.put();
            if(!success){
                System.out.println("Flow control for a while");
                Thread.sleep(20);
            }else{
                System.out.println("Put ticket...");
                Thread.sleep(new Random().nextInt(10));
            }
        }
        leakyBucket.close();
    }
}
