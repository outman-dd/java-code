package code.collection.bitmap;

import code.collection.hashtable.HashMap;

import java.util.BitSet;

/**
 * 〈位图〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/16
 */
public class BitMap {

    private BitSet bitSet;

    private int capacity;

    private HashMap<Integer, Integer> conflictCounter;

    public BitMap(int capacity) {
        this.capacity = capacity;
        bitSet = new BitSet(capacity);
        conflictCounter = new HashMap<>(capacity / 16);
    }

    public void set(int n){
        if (get(n)) {
            Integer count = conflictCounter.get(n);
            if (count == null) {
                conflictCounter.put(n, 2);
            } else {
                conflictCounter.put(n, ++count);
            }
        } else {
            bitSet.set(n);
        }
    }

    public boolean get(int n){
        return bitSet.get(n);
    }

    public static void main(String[] args) {
        int[] a = {10, 3, 4, 30, 9, 29, 60, 13, 40, 43, 3};
        int capacity = a.length * 10;
        BitMap bitMap = new BitMap(capacity);
        for (int i : a) {
            bitMap.set(i);
        }
        for (int i = 0; i < capacity; i++) {
            if(bitMap.get(i)){
                Integer count = bitMap.conflictCounter.get(i);
                count = count == null ? 1 : count;
                for (Integer j = 0; j < count; j++) {
                    System.out.print(i);
                    System.out.print("\t");
                }
            }
        }
    }
}
