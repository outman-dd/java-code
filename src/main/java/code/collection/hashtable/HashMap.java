package code.collection.hashtable;

import java.util.UUID;

/**
 * 〈散列表〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/28
 */
public class HashMap<K, V> {

    private Entry<K, V>[] slots;

    private int capacity;

    private int size;

    private int threshold;

    private double loadFactor = 0.75;

    public HashMap() {
        this(16);
    }

    public HashMap(int initCapacity) {
        this.init(initCapacity);
        this.slots = new Entry[capacity];
    }

    private void init(int initCapacity) {
        this.capacity = get2Power(initCapacity);
        this.threshold = (int) (capacity * loadFactor);
        System.out.println("Init, capacity=" + capacity + ", threshold=" + threshold);
    }

    public int get2Power(int capacity) {
        int n = capacity - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n + 1;
    }

    private class Entry<K, V> {

        private K key;

        private V value;

        private Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    public V get(K key) {
        Entry<K, V> entry = getEntry(key);
        return entry != null ? entry.value : null;
    }

    private Entry<K, V> getEntry(K key) {
        int h = hash(key);
        int index = indexOf(h, capacity);
        if (slots[index] == null) {
            return null;
        }
        return inLinkedList(index, key);
    }

    public boolean contains(K key) {
        return getEntry(key) != null;
    }

    public void put(K key, V value) {
        int h = hash(key);
        int index = indexOf(h, capacity);

        putInner(key, value, h, index);
    }

    private void putInner(K key, V value, int h, int index) {
        if (slots[index] == null) {
            slots[index] = new Entry<K, V>(key, value, null);
            size++;
        }

        Entry<K, V> existed = inLinkedList(index, key);
        if (existed != null) {
            existed.value = value;
        } else {
            // hash冲突
            if (size >= threshold) {
                //满足扩容条件
                resize();
                putInner(key, value, h, indexOf(h, capacity));
            } else {
                // jdk1.7采用头插法，1.8改用尾插法（防止在并发扩容时, 导致逆序，链表成环的问题）
                // 当链表长大于8的时候，转换成红黑树（logN）
                slots[index] = new Entry<>(key, value, slots[index]);
                size++;
            }
        }
    }


    /**
     * 触发扩容 必须同时满足2个条件：
     * 当前数据存储的数量size必须 >= 阈值；
     * 当前加入的数据是否发生了hash冲突
     */
    public void resize() {
        int newCapacity = capacity << 1;
        Entry<K, V>[] newSlots = new Entry[newCapacity];
        for (Entry<K, V> slot : slots) {
            while (slot != null) {
                int idx = indexOf(hash(slot.key), newCapacity);
                if (newSlots[idx] == null) {
                    newSlots[idx] = new Entry(slot.key, slot.value, null);
                } else {
                    newSlots[idx] = new Entry(slot.key, slot.value, newSlots[idx]);
                }
                slot = slot.next;
            }
        }
        slots = newSlots;
        capacity = newCapacity;
        threshold = (int) (capacity * loadFactor);
        System.out.println("Resize, size=" + size + ", capacity=" + capacity + ", threshold=" + threshold);
        System.out.println(this);
    }

    /**
     * key是否在链表上
     *
     * @param index
     * @param key
     * @return
     */
    private Entry<K, V> inLinkedList(int index, Object key) {
        Entry<K, V> entry = slots[index];
        do {
            if (entry.key.equals(key)) {
                return entry;
            }
        } while ((entry = entry.next) != null);
        return null;
    }

    /**
     * 对hashcode做扰动计算
     * 为了把高位的特征和低位的特征组合起来，降低哈希冲突的概率，
     * 也就是说，尽量做到任何一位的变化都能对最终得到的结果产生影响。
     * jdk 7与8实现不同
     *
     * @param key
     * @return
     */
    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 散列函数，定位到数组中的位置
     * 数组大小为2^n，为了使用位运算 h % length = h & (length-1)
     *
     * @param h
     * @return
     */
    private int indexOf(int h, int length) {
        return h & (length - 1);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slots.length; i++) {
            sb.append("[").append(i).append("]");
            if (slots[i] == null) {
                sb.append("->");
            } else {
                Entry<K, V> entry = slots[i];
                do {
                    sb.append("->").append("(")
                            .append(entry.key).append("=>").append(entry.value)
                            .append(")");
                } while ((entry = entry.next) != null);
            }
            sb.append("\n\r");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        HashMap<String, Integer> hashMap = new HashMap<>(8);
        for (int i = 0; i < 8; i++) {
            hashMap.put(UUID.randomUUID().toString().substring(0, 6), i);
        }
        System.out.println("Size: " + hashMap.size);
        System.out.println(hashMap);

        for (int i = 0; i < 12; i++) {
            hashMap.put(UUID.randomUUID().toString().substring(0, 6), i);
        }
        System.out.println("Size: " + hashMap.size);
        System.out.println(hashMap);
    }
}
