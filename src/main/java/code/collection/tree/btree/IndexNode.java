package code.collection.tree.btree;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 非叶子节点，索引节点
 * 假设keywords=[3, 5, 8, 10]
 * 4个键值将数据分为5个区间:(-INF,3), [3,5), [5,8), [8,10), [10,INF)
 * 5个区间分别对应:children[0]...children[4]
 * m值是事先计算得到的，计算的依据是让所有信息的大小正好等于页的大小:
 * PAGE_SIZE = (m-1)*4*[keywords大小] + m*8*[children大小]
 *
 * @author zixiao
 * @date 2019/12/20
 */
public class IndexNode extends TreeNode {

    /**
     * 保存子节点指针
     */
    private TreeNode[] children;

    public IndexNode() {
        keys = new Integer[m];
        children = new TreeNode[m+1];
    }

    public IndexNode(int key, TreeNode left, TreeNode right) {
        this();
        insert(key, left, right);
    }

    public TreeNode[] children(){
        return children;
    }

    public TreeNode findChild(int key){
        if (key < keys[0]) {
            return children[0];
        } else if (key == keys[0]) {
            return children[1];
        }
        for (int i = 1; i < keySize; i++) {
            if (key > keys[i-1] && key < keys[i]) {
                return children[i];
            }else if(key == keys[i]){
                return children[i+1];
            }
        }
        return children[keySize];
    }

    public boolean insert(int key, TreeNode left, TreeNode right){
        keys[keySize] = key;
        children[keySize] = left;
        children[keySize+1] = right;
        keySize++;
        return true;
    }

    public boolean delete(int idx){
        keys[idx] = null;
        children[idx+1] = null;
        keySize--;
        return true;
    }

    /**
     * 将这个索引类型结点分裂成两个索引结点，
     * 以中间位置的key划分两半，左索引结点包含一半key，右索引结点包后一半的key，
     * 将中间位置的key进位到父结点中， 并把进位节点的左孩子指向左结点, 进位到父结点的key右孩子指向右结点。
     * 将当前结点的指针指向父结点，然后重复本步骤。
     * m=5
     * 0 1 2 3 4
     * 左索引结点：0， 1； 右索引结点 3，4； 2进位到父节点
     *
     * @return
     */
    public Pair<TreeNode, Integer> split(){
        IndexNode newNode = new IndexNode();
        //提取中间节点的key
        int indexKey = keys[m/2];
        //从中间节点后一个节点开始拷贝
        for (int i = m/2 + 1; i < keys.length; i++) {
            newNode.insert(keys[i], children[i], children[i+1]);
        }
        //从中间节点（包括）开始删除
        for (int i = m/2; i < keys.length; i++) {
            delete(i);
        }
        return Pair.of(newNode, indexKey);
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
