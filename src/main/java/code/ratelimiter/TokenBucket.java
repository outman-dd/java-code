package code.ratelimiter;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 〈令牌桶〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/3/21
 */
public class TokenBucket {

    private final BlockingQueue queue;

    private final ScheduledExecutorService scheduler;

    private final Object token = new Object();

    private final int MILLS = 1000;

    /**
     * 时间轮
     * 每个slot存放 需要生产的token数
     */
    private final int[] timeWheel;

    /**
     * slot数
     */
    private int slotNum = 20;

    /**
     * 当前slot位置
     */
    private int currentSlot = 0;

    private TokenBucket(int limitNum){
        queue = new ArrayBlockingQueue(limitNum);
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
                    queue.offer(token);
                }
            }
        }, period, period, TimeUnit.MILLISECONDS);
    }

    public static TokenBucket create(int limitNum){
        if(limitNum <= 0){
            throw new IllegalArgumentException("The limitNum must be greater than 0 ");
        }
        TokenBucket tokenBucket = new TokenBucket(limitNum);
        tokenBucket.init(limitNum);
        return tokenBucket;
    }

    /**
     * 获取token，立即返回
     * @return
     */
    public Object get(){
        return queue.poll();
    }

    /**
     * 获取token，等待timeout返回
     * @param timeout 等待时间
     * @param unit
     * @return
     * @throws InterruptedException
     */
    public Object get(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    /**
     * 获取token，如果没有可用token，则阻塞等待
     * @return
     * @throws InterruptedException
     */
    public Object getWithBlocking() throws InterruptedException {
        return queue.take();
    }

    public void close(){
        scheduler.shutdown();
    }

    private void print(){
        for(int i=0; i<slotNum; i++){
            System.out.println("slot: "+i+", num:"+timeWheel[i]);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucket tokenBucket = TokenBucket.create(111);
        tokenBucket.print();
        Thread.sleep(1000);

        for(int i=0; i<500; i++){
            Object ticket = tokenBucket.getWithBlocking();
            if(ticket == null){
                System.out.println("Flow control for a while");
                Thread.sleep(20);
            }else{
                System.out.println("Get ticket...");
                Thread.sleep(new Random().nextInt(10));
            }
        }
        tokenBucket.close();
    }
}
