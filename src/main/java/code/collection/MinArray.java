package code.collection;

import java.util.*;

/**
 * 〈最小数组〉<p>
 * 储存最大的k个值，满足公平性，即当值相等时，保留先放入的值
 *
 * 实现：
 * 0、新建容量为k的数组
 * 1、依次存入k个值
 * 2、当数组满后，待放入值x，如果x大于数组中最小值min，则替换min；否则，丢弃该值
 * 复杂度：O（n*k）
 *
 * @author zixiao
 * @date 2019/2/21
 */
public class MinArray<T> {

    private int maxSize;

    private List<T> dataList;

    private Comparator<T> comparator;

    private Entry currentMin;

    public MinArray(int maxSize) {
        this(maxSize, null);
    }

    public MinArray(int maxSize, Comparator<T> comparator) {
        this.maxSize = maxSize;
        this.comparator = comparator;
        this.dataList = new ArrayList<>(maxSize);
    }

    public void add(T value){
        if(dataList.size() < maxSize){
            dataList.add(value);
        }else{
            if(currentMin == null){
                currentMin = findMin();
            }
            if(compare(value, currentMin.object) > 0){
                dataList.set(currentMin.index, value);
                currentMin = null;
            }
        }
    }

    public List<T> getList(){
        return dataList;
    }

    public List<T> sortedList(){
        Collections.sort(dataList, comparator);
        Collections.reverse(dataList);
        return dataList;
    }

    /**
     * 复杂度 O(k)
     * @return
     */
    private Entry findMin() {
        T min = dataList.get(0);
        int minIndex = 0;
        int size = dataList.size();
        for(int i=1; i<size; i++){
            T temp = dataList.get(i);
            if(compare(min, temp) > 0) {
                min = temp;
                minIndex = i;
            }
        }
        return new Entry(minIndex, min);
    }

    private int compare(T a, T b){
        if(comparator == null){
            Comparable<? super T> ca = (Comparable<? super T>) a;
            return ca.compareTo(b);
        }else{
            return comparator.compare(a, b);
        }
    }

    private class Entry{
        int index;

        T object;

        public Entry(int index, T object) {
            this.index = index;
            this.object = object;
        }
    }

    public static void main(String[] args) {
        MinArray<Integer> minArray = new MinArray<>(5);
        for (int i = 0; i<20; i++){
            int num = new Random().nextInt(100);
            minArray.add(num);
            System.out.print(num);
            System.out.print(",");
        }
        System.out.println();
        minArray.sortedList().forEach(System.out::println);
    }
}
