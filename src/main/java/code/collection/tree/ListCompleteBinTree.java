package code.collection.tree;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/10
 */
public class ListCompleteBinTree<E extends Comparable> extends AbstractTree<E> {

    private Node<E> root;

    @Data
    @AllArgsConstructor
    class Node<E> {

        private E value;

        private Node<E> left;

        private Node<E> right;
    }

    public ListCompleteBinTree(E r) {
        this.root = new Node<E>(r, null, null);
    }

    public boolean put(E e) {
        Node node = root;
        put(node, e);
        return true;
    }

    /**
     * 返回值所在节点
     * @param node
     * @param e
     * @return
     */
    private Node<E> put(Node<E> node, E e) {
        return null;
    }

    public void print() {
        StringBuilder sb = new StringBuilder();

    }

    public static void main(String[] args) {
        ListCompleteBinTree<Integer> tree = new ListCompleteBinTree(8);
        tree.put(5);
        tree.put(7);
        tree.put(2);
        tree.put(10);
        tree.put(3);
        tree.put(8);
        tree.put(9);

        tree.print();
    }
}
