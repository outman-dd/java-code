package code.collection;

import java.util.*;

/**
 * 〈最小堆〉<p>
 * 固定容量，解决TopK问题，满足公平性，即当值相等时，保留先放入的值
 *
 * @author zixiao
 * @date 2019/2/21
 */
public class MinHeap<E> extends PriorityQueue<E> {

    private int maxSize;

    public MinHeap(int maxSize){
        this(maxSize, null);
    }

    public MinHeap(int maxSize, Comparator<E> comparator){
        super(maxSize, comparator);
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(E e) {
        if(size() < maxSize){
            return super.add(e);
        }
        E min = peek();
        if(compare(e, min) <= 0){
            return false;
        }
        poll();
        return add(e);
    }

    private int compare(E a, E b){
        if(comparator() == null){
            Comparable<? super E> ca = (Comparable<? super E>) a;
            return ca.compareTo(b);
        }else{
            return comparator().compare(a, b);
        }
    }

    public List<E> sortedList(){
        List<E> list = new ArrayList<>(this);
        Collections.sort(list, comparator());
        Collections.reverse(list);
        return list;
    }

    public static void main(String[] args) {
        MinHeap<Integer> minHeap = new MinHeap<>(5);
        for (int i = 0; i<20; i++){
            int num = new Random().nextInt(100);
            minHeap.add(num);
            System.out.print(num);
            System.out.print(",");
        }
        System.out.println();
        minHeap.sortedList().forEach(System.out::println);
    }


}
