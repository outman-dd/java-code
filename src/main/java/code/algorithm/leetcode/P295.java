package code.algorithm.leetcode;

import java.util.PriorityQueue;

/**
 题目描述
 中位数是有序列表中间的数。如果列表长度是偶数，中位数则是中间两个数的平均值。

 例如，

 [2,3,4] 的中位数是 3

 [2,3] 的中位数是 (2 + 3) / 2 = 2.5

 *
 * @author zixiao
 * @date 2020/1/16
 */
public class P295 {

    public static class MedianFinder{
        private PriorityQueue<Integer> maxHeap = new PriorityQueue<>((o1, o2) -> o2-o1);

        private PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        /**
         * 从数据流中添加一个整数到数据结构中
         * 小的数放maxHeap，大的数放minHeap
         * @param num
         */
        void addNum(int num){
            //1 先放最大堆
            maxHeap.add(num);
            //2 取出最大堆得最大数放入最小堆，确保最大堆的数都小于最小堆
            minHeap.add(maxHeap.poll());
            //3 确保两个堆的数量差不超过1
            if(maxHeap.size() < minHeap.size()){
                maxHeap.add(minHeap.poll());
            }
            System.out.println("add " + num);
        }

        /**
         * 返回目前所有元素的中位数
         * @return
         */
        double findMedian(){
            if(maxHeap.size() > minHeap.size()){
                return maxHeap.peek();
            } else if (maxHeap.size() < minHeap.size()){
                return minHeap.peek();
            } else {
                return (maxHeap.peek()+minHeap.peek())*0.5d;
            }
        }
    }

    public static void main(String[] args) {
        MedianFinder p295 = new MedianFinder();
        p295.addNum(1);
        p295.addNum(2);
        System.out.println("median: "+ p295.findMedian());

        /**
         * input： 41 35 62 4 97 108
         * output：41 38 41 38 41 51.5
         */
        MedianFinder medianFiner  = new MedianFinder();
        medianFiner.addNum(41);
        System.out.println("median: "+ medianFiner.findMedian());

        medianFiner.addNum(35);
        System.out.println("median: "+ medianFiner.findMedian());

        medianFiner.addNum(62);
        System.out.println("median: "+ medianFiner.findMedian());

        medianFiner.addNum(4);
        System.out.println("median: "+ medianFiner.findMedian());

        medianFiner.addNum(97);
        System.out.println("median: "+ medianFiner.findMedian());

        medianFiner.addNum(108);
        System.out.println("median: "+ medianFiner.findMedian());
        for (String i : args) {

        }
    }
}
