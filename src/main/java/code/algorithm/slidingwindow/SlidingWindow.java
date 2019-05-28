package code.algorithm.slidingwindow;

import java.io.Closeable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 〈滑动窗口〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/5/24
 */
public class SlidingWindow implements Closeable{

    /**
     * 节点数据
     */
    private SlidingNode[] nodes;

    /**
     * slot数
     */
    private int slotNum;

    /**
     * 当前slot位置
     */
    private int currentSlot = 0;

    /**
     * 窗口大小
     */
    private int windowSize;

    /**
     * 单节点的周期（秒）
     */
    private int nodePeriod;

    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    public SlidingWindow(int slotNum, int windowSize){
        this(slotNum, windowSize, 1);
    }

    public SlidingWindow(int slotNum, int windowSize, int nodePeriod){
        this.slotNum = slotNum;
        this.windowSize = windowSize;
        this.nodePeriod = nodePeriod;

        this.nodes = new SlidingNode[slotNum];
        this.scheduler = new ScheduledThreadPoolExecutor(1);

        this.init();
    }

    private void init(){
        for(int i=0; i<slotNum; i++){
            nodes[i] = new SlidingNode(nodePeriod);
        }

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                slidingToNext();
            }
        }, nodePeriod, nodePeriod, TimeUnit.SECONDS);
    }

    /**
     * 滑动到下一个窗口
     */
    private void slidingToNext(){
        int nextSlot = currentSlot;
        if (nextSlot >= (slotNum-1)) {
            nextSlot = 0;
        }else{
            nextSlot++;
        }
        //清除数据
        nodes[nextSlot].reset();
        currentSlot = nextSlot;
    }

    /**
     * 上一个窗口
     * @return
     */
    public SlidingNode lastWindow(){
        int slotIndex = currentSlot;
        SlidingNode total = new SlidingNode(windowSize * nodePeriod);
        for (int i=0; i< windowSize; i++){
            slotIndex = lastNodeIndex(slotIndex);
            total.addCount(nodes[slotIndex].totalCount());
            total.addRt(nodes[slotIndex].totalRt());
        }
        return total;
    }

    /**
     * 上一个节点位置
     * @param slotIndex
     * @return
     */
    private int lastNodeIndex(int slotIndex){
        return (slotIndex == 0) ? (slotNum-1) : (slotIndex - 1);
    }

    @Override
    public void close(){
        scheduler.shutdownNow();
    }

    public SlidingNode current(){
        return nodes[currentSlot];
    }

}
