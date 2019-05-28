package code.algorithm.slidingwindow;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * 〈滑动节点〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/24
 */
public class SlidingNode implements Serializable{

    /**
     * 周期秒数
     */
    private int period;

    /**
     * 总数量
     */
    private AtomicInteger totalCount = new AtomicInteger();

    /**
     * 总RT
     */
    private LongAdder totalRt = new LongAdder();

    public SlidingNode(int period) {
        this.period = period;
    }

    public int getPeriod(){
        return period;
    }

    public void addCount(int count){
        totalCount.addAndGet(count);
    }

    public void addRt(long rt){
        totalRt.add(rt);
    }

    public int totalCount(){
        return totalCount.get();
    }

    public long totalRt(){
        return totalRt.longValue();
    }

    public long avgRt(){
        return totalCount() == 0 ? 0 : totalRt() / totalCount();
    }

    public int tps(){
        return totalCount() / getPeriod();
    }

    /**
     * 重置计数
     */
    public void reset(){
        totalCount.set(0);
        totalRt.reset();
    }

    @Override
    public String toString() {
        return "SlidingNode{" +
                "period=" + period +
                ", totalCount=" + totalCount() +
                ", totalRt=" + totalRt() +
                '}';
    }

    public String prettyPrint(){
        return String.format("TotalSec=%s, totalCount=%s, TPS=%s, totalRT=%s, avgRT=%s",
                getPeriod(), totalCount(), tps(), totalRt(), avgRt());
    }
}
