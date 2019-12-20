package code.collection.tree;

import java.util.Arrays;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/10
 */
public class ArrayCompleteBinTree<E> {

    private Object[] nodes;

    private int capacity;

    private int current = 0;

    public ArrayCompleteBinTree(E root) {
        this(8, root);
    }

    public ArrayCompleteBinTree(int capacity, E root) {
        this.capacity = capacity;
        nodes = new Object[capacity + 1];
        put(root);
    }

    public boolean put(E e) {
        if (current == capacity) {
            resize();
            System.out.println("Tree resize to " + capacity);
        }
        current++;
        nodes[current] = e;
        return true;
    }

    private void resize() {
        int newCapacity = 2 * capacity;
        Object[] newNodes = Arrays.copyOf(nodes, newCapacity + 1);
        nodes = newNodes;
        capacity = newCapacity;
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        int len = current + 1;
        while (i < len) {
            int doubleI = i * 2;
            while (i < doubleI && i < len) {
                sb.append(nodes[i]).append("\t");
                i++;
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    public static void main(String[] args) {
        ArrayCompleteBinTree<String> tree = new ArrayCompleteBinTree("A");
        tree.put("B");
        tree.put("C");
        tree.put("D");
        tree.put("E");
        tree.put("F");
        tree.put("G");
        tree.put("H");
        tree.put("I");

        tree.print();
    }
}
