package code.ratelimiter.impl;

import code.ratelimiter.RateLimiter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 〈限流策略 - 匀速排队〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/28
 */
public class ConstantSpeedLimiter implements RateLimiter {

    private final int maxQueueingTimeMs = 500;

    /**
     * QPS limit
     */
    private final int count;

    private final AtomicLong latestPassedTime = new AtomicLong(-1);

    public ConstantSpeedLimiter(int count) {
        this.count = count;
    }

    @Override
    public boolean canPass(int acquireCount) {
        // Pass when acquire count is less or equal than 0.
        if (acquireCount <= 0) {
            return true;
        }
        // Reject when count is less or equal than 0.
        // Otherwise,the costTime will be max of long and waitTime will overflow in some cases.
        if (count <= 0) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        // Calculate the interval between every two requests.
        long costTime = Math.round(1.0 * (acquireCount) / count * 1000);

        // 预期请求到达时间
        long expectedTime = costTime + latestPassedTime.get();

        //当前时间 大于 预期到达时间，说明请求来的慢了
        if (expectedTime <= currentTime) {
            // Contention may exist here, but it's okay.
            latestPassedTime.set(currentTime);
            return true;
        } else {
            //计算当前时间与预期到达时间的差值, 即等待时间
            long waitTime = costTime + latestPassedTime.get() - System.currentTimeMillis();
            //差值过大，说明请求来的太快了，拒掉吧
            if (waitTime > maxQueueingTimeMs) {
                return false;
            }
            //latestPassedTime设置为预期时间
            long oldTime = latestPassedTime.addAndGet(costTime);
            //重新计算当前时间与预期到达时间的差值
            waitTime = oldTime - System.currentTimeMillis();
            if (waitTime > maxQueueingTimeMs) {
                //差值还是过大，拒掉吧，时间还原
                latestPassedTime.addAndGet(-costTime);
                return false;
            }
            // 太快就等待waitTime，竞争下waitTime可能 <= 0
            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        ConstantSpeedLimiter constantSpeedLimiter = new ConstantSpeedLimiter(100);
        for (int i = 0; i < 500; i++) {
            constantSpeedLimiter.canPass(1);
        }
    }
}
