package code.collection.tree;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 〈二分查找树〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/10
 */
public class BinarySearchTree<E extends Comparable> extends AbstractTree<E> {

    private Node<E> root;

    @Data
    @AllArgsConstructor
    class Node<E> {

        private E value;

        private Node<E> left;

        private Node<E> right;
    }

    @Data
    @AllArgsConstructor
    class Pair<E> {
        private Node<E> parent;

        private Node<E> node;
    }

    public BinarySearchTree(E r) {
        this.root = new Node<E>(r, null, null);
    }

    public boolean put(E e) {
        Node node = root;
        put(node, e);
        return true;
    }

    /**
     * 返回值所在节点
     *
     * @param node
     * @param e
     * @return
     */
    private Node<E> put(Node<E> node, E e) {
        int compare = e.compareTo(node.value);
        if (compare == 0) {
            System.out.println(e + " 已存在");
            return node;
        } else if (compare < 0) {
            if (node.left != null) {
                return put(node.left, e);
            } else {
                node.left = new Node<>(e, null, null);
                return node.left;
            }
        } else {
            if (node.right != null) {
                return put(node.right, e);
            } else {
                node.right = new Node<>(e, null, null);
                return node.right;
            }
        }
    }

    public boolean exist(E e) {
        return find(null, root, e) != null;
    }

    private Pair<E> find(Node parent, Node node, E e) {
        if (node == null) {
            return null;
        }
        int compare = e.compareTo(node.value);
        if (compare == 0) {
            return new Pair(parent, node);
        } else if (compare < 0) {
            return find(node, node.left, e);
        } else {
            return find(node, node.right, e);
        }
    }


    public boolean delete(E e) {
        Pair<E> nodePair = find(null, root, e);
        if(nodePair == null){
            System.out.println("待删除节点不存在，" + e);
            return false;
        }
        deleteNode(nodePair.getParent(), nodePair.getNode());
        return true;
    }

    private void deleteNode(Node<E> parent, Node<E> node) {
        if (node.left == null && node.right == null) {
            //如果没有子节点
            setNodeDeleted(parent, node, null);
        } else if (node.left == null && node.right != null) {
            //如果没有左子节点
            setNodeDeleted(parent, node, node.right);
        } else if (node.right == null && node.left != null) {
            //如果没有右子节点
            setNodeDeleted(parent, node, node.left);
        } else {
            //查找右子树最小节点
            Pair<E> pair = findMin(node, node.right);
            Node<E> rightMin = pair.getNode();
            //当前节点值设置为右子树最小节点的值
            node.value = rightMin.value;
            //删除右子树最小节点
            deleteNode(pair.getParent(), rightMin);
        }
    }

    private void setNodeDeleted(Node<E> parent, Node<E> node, Node<E> child){
        if(parent.left == node){
            parent.left = child;
        }else{
            parent.right = child;
        }
    }

    private Pair<E> findMin(Node parent, Node node) {
        if (node.left == null) {
            return new Pair(parent, node);
        } else {
            return findMin(node, node.left);
        }
    }

    public int layer(){
        return getLayer(root, 0);
    }

    private int getLayer(Node node, int layer){
        if(node == null){
            return layer;
        }
        layer++;
        return Math.max(getLayer(node.left, layer), getLayer(node.right, layer));
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        append(root, sb);
        System.out.println(sb);
    }

    /**
     * 前序遍历
     * @param node
     * @param sb
     */
    public void append(Node node, StringBuilder sb){
        if(node == null){
            return;
        } else {
            sb.append(node.value).append("\t");
        }
        append(node.left, sb);
        append(node.right, sb);
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<Integer>(33);
        tree.put(16);
        tree.put(50);
        tree.put(13);
        tree.put(18);
        tree.put(34);
        tree.put(58);
        tree.put(15);
        tree.put(17);
        tree.put(25);
        tree.put(51);
        tree.put(66);
        tree.put(19);
        tree.put(27);
        tree.put(55);
        System.out.println("Layer:" + tree.layer());
        tree.print();

        System.out.println("1:" + tree.exist(17));
        System.out.println("2:" + tree.exist(19));
        tree.delete(18);
    }
}
