package code.collection.tree.btree;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 〈B+树〉<p>
 * h = log m (N)， m越大，h越小
 * @author zixiao
 * @date 2019/12/20
 * @see https://blog.csdn.net/Fmuma/article/details/80287924
 */
public class BPlusTree {

    private TreeNode root;

    public static int m;

    public BPlusTree(int m) {
        BPlusTree.m = m;
    }

    public BPlusTree() {
        this(5);
    }

    public boolean insert(int key, long dataAddr) {
        LeafNode leaf = null;
        List<IndexNode> parents = null;
        if (root == null) {
            leaf = new LeafNode();
            root = leaf;
            parents = Collections.EMPTY_LIST;
        } else {
            Pair<LeafNode, List<IndexNode>> pair = findLeaf(key);
            leaf = pair.getKey();
            parents = pair.getRight();
        }
        TreeNode highNode = insertLeaf(parents, leaf, key, dataAddr);
        if (highNode != null) {
            root = highNode;
        }
        return true;
    }

    /**
     * 插入数据到叶子
     *
     * @param parents
     * @param leaf
     * @param key
     * @param dataAddr
     * @return 新生成的最高节点
     */
    private TreeNode insertLeaf(List<IndexNode> parents, LeafNode leaf, int key, long dataAddr) {
        leaf.insert(key, dataAddr);
        if (leaf.keySize() < leaf.m()) {
            return null;
        }
        // 若当前结点key的个数小于等于m-1，叶子结点分裂成左右两个叶子结点
        return split(parents, leaf);
    }

    /**
     * 节点node分裂
     *
     * @param parents
     * @param node
     * @return
     */
    private TreeNode split(List<IndexNode> parents, TreeNode node) {
        Pair<TreeNode, Integer> pair = node.split();
        TreeNode node2 = pair.getKey();
        int newIndexKey = pair.getValue();

        if (parents.isEmpty()) {
            return new IndexNode(newIndexKey, node, node2);
        }
        return insertIndex(parents.subList(1, parents.size()), parents.get(0), newIndexKey, node, node2);
    }

    /**
     * 插入key到索引节点
     *
     * @param parents
     * @param node
     * @param key
     * @param left
     * @param right
     * @return 新生成的最高节点
     */
    private TreeNode insertIndex(List<IndexNode> parents, IndexNode node, int key, TreeNode left, TreeNode right) {
        node.insert(key, left, right);
        if (node.keySize() < node.m()) {
            return null;
        }
        // 若当前结点key的个数小于等于m-1，索引结点分裂成左右两个索引结点
        return split(parents, node);
    }

    /**
     * 查找叶关键字对应的叶子节点，及索引父节点
     *
     * @param key
     * @return
     */
    private Pair<LeafNode, List<IndexNode>> findLeaf(int key) {
        TreeNode node = root;
        List<IndexNode> parents = new ArrayList<>();
        while (!node.isLeaf()) {
            parents.add((IndexNode) node);
            node = ((IndexNode) node).findChild(key);
        }
        Collections.reverse(parents);
        return Pair.of((LeafNode) node, parents);
    }

    public Long get(int key) {
        LeafNode node = findLeafNode(key);
        for (int i = 0; i < node.keySize(); i++) {
            if (node.keys()[i].equals(key)) {
                return node.getDataAddr()[i];
            }
        }
        return null;
    }

    /**
     * 查找叶关键字对应的叶子节点
     *
     * @param key
     * @return
     */
    private LeafNode findLeafNode(int key) {
        TreeNode node = root;
        while (!node.isLeaf()) {
            node = ((IndexNode) node).findChild(key);
        }
        return (LeafNode) node;
    }

    public List<Long> range(int fromKey, int toKey) {
        List<Long> dataList = new ArrayList<>();
        LeafNode from = findLeafNode(fromKey);
        Integer fromMaxKey = from.keys[from.keySize - 1];

        //1 数据在一个节点上
        if (fromMaxKey >= toKey) {
            for (int i = 0; i < from.keySize; i++) {
                if (from.keys[i] >= fromKey && from.keys[i] <= toKey) {
                    dataList.add(from.getDataAddr()[i]);
                }
            }
            return dataList;
        }

        //2 数据分布在多个节点上
        LeafNode to = findLeafNode(toKey);
        LeafNode node = from;
        while (node != null) {
            if (node != from && node != to) {
                //a 既不是开始又不是结束节点，则所有数据都加上
                for (int i = 0; i < node.keySize; i++) {
                    dataList.add(node.getDataAddr()[i]);
                }
            } else if (node == from) {
                //b 开始节点，只取大于fromKey的数据
                for (int i = 0; i < node.keySize; i++) {
                    if (node.keys[i] >= fromKey) {
                        dataList.add(node.getDataAddr()[i]);
                    }
                }
            } else if (node == to) {
                //c 结束节点，只取小于toKey的数据
                for (int i = 0; i < node.keySize; i++) {
                    if (node.keys[i] <= toKey) {
                        dataList.add(node.getDataAddr()[i]);
                    }
                }
                break;
            }
            node = node.getNext();
        }
        return dataList;
    }

    public void print() {
        StringBuilder sb = new StringBuilder();
        append(sb, Lists.newArrayList(root));
        System.out.println(sb);
    }

    private void append(StringBuilder sb, List<TreeNode> nodeList) {
        List<TreeNode> children = new ArrayList<>();
        for (TreeNode node : nodeList) {
            if (node == null) {
                continue;
            } else if (node.isLeaf()) {
                sb.append("[");
                joinKeys(sb, ",", node.keys());
                sb.append("]").append("->");
            } else {
                children.addAll(Lists.newArrayList(((IndexNode) node).children()));
                sb.append("[");
                joinKeys(sb, " ", node.keys());
                sb.append("]");
                sb.append("\t");
            }
        }
        sb.append("\n\r");
        if (!children.isEmpty()) {
            append(sb, children);
        }
    }

    private void joinKeys(StringBuilder sb, CharSequence delimiter, Integer[] keys) {
        boolean first = true;
        for (Integer key : keys) {
            if (key == null) {
                break;
            }
            if (first) {
                first = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(key);
        }
    }

    public static void main(String[] args) {
        BPlusTree bPlusTree = new BPlusTree(5);
        /**
         * m=5
         *   				  [11]
         *      [7      9]              [13       15]
         * [5,6]->[7,8]->[9,10]->[11,12]->[13,14]->[15,16,17]
         */
        for (int i = 5; i <= 17; i++) {
            bPlusTree.insert(i, i+100);
        }
        bPlusTree.print();

        /**
         * 				          [11            ,           17]
         * 		  [7,     9]		       [13,     15]               [19,     21]
         *   [5,6]->[7,8] ->[9,10]->[11,12]->[13,14]->[15,16]->[17,18]->[19,20]->[21,22,23]
         */
        for (int i = 18; i <= 23; i++) {
            bPlusTree.insert(i, i+100);
        }
        bPlusTree.print();

        /**
         *                      [11                       17                         23]
         *       [7     9]	            [13      15]	           [19      21]	              [25       27      29]
         * [5,6]->[7,8]->[9,10]->[11,12]->[13,14]->[15,16]->[17,18]->[19,20]->[21,22]->[23,24]->[25,26]->[27,28]->[29,30,31]->
         */
        for (int i = 24; i <= 31; i++) {
            bPlusTree.insert(i, i+100);
        }
        bPlusTree.print();

        printGet(bPlusTree,4);
        printGet(bPlusTree,9);
        printGet(bPlusTree,23);

        printRange(bPlusTree,5, 6);
        printRange(bPlusTree,17, 21);
        printRange(bPlusTree,29, 40);
        printRange(bPlusTree,32, 40);

        for (int i = 32; i <= 100; i++) {
            bPlusTree.insert(i, i+100);
        }
        bPlusTree.print();
    }

    private static void printGet(BPlusTree bPlusTree, int key){
        System.out.println("Get key " + key + ": " + bPlusTree.get(key));
    }

    private static void printRange(BPlusTree bPlusTree, int from, int to){
        System.out.print("Range key [" + from + ", " + to + "]: ");
        bPlusTree.range(from, to).forEach(l -> System.out.print(l + " "));
        System.out.println();
    }

}
