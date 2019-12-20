package code.collection.graph;

/**
 * 〈邻接表〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/16
 */
public class AdjacencyList {

    private Entry[] slots;

    private int capacity;

    private class Entry {

        private int value;

        private Entry next;

        public Entry(int value, Entry next) {
            this.value = value;
            this.next = next;
        }
    }

    public AdjacencyList(int capacity) {
        this.capacity = capacity;
        this.slots = new Entry[this.capacity];
    }

    public void put(int a, int b) {
        putDirected(a, b);
    }

    /**
     * 有向图
     *
     * @param a
     * @param b
     */
    private void putDirected(int a, int b) {
        Entry entry = slots[a - 1];
        if (entry == null) {
            slots[a - 1] = new Entry(b, null);
        } else {
            while (entry.next != null) {
                entry = entry.next;
            }
            entry.next = new Entry(b, null);
        }
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < slots.length; i++) {
            sb.append("[").append(i + 1).append("]");
            Entry entry = slots[i];
            while (entry != null) {
                sb.append("->").append(entry.value);
                entry = entry.next;
            }
            sb.append("/").append("\n\r");
        }
        System.out.println(sb);
    }

    public static void main(String[] args) {
        /**
         * 有向图
         * 1---->2---->3
         * ^    ^|    ^
         * |   / |   /
         * |  /  |  /
         * | v   v /
         * 4<---- 5
         */
        System.out.println("--------------邻接表");
        AdjacencyList list = new AdjacencyList(5);
        list.put(1, 2);
        list.put(2, 3);
        list.put(2, 4);
        list.put(2, 5);
        list.put(4, 1);
        list.put(4, 2);
        list.put(5, 4);
        list.put(5, 3);
        list.print();

        /**
         * 逆序邻接表
         */
        System.out.println("--------------逆邻接表");
        AdjacencyList reverseList = new AdjacencyList(5);
        reverseList.put(2, 1);
        reverseList.put(3, 2);
        reverseList.put(4, 2);
        reverseList.put(5, 2);
        reverseList.put(1, 4);
        reverseList.put(2, 4);
        reverseList.put(4, 5);
        reverseList.put(3, 5);
        reverseList.print();


    }
}
