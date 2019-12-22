package code.collection.tree.btree;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 〈基础节点〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/20
 */
public abstract class TreeNode {

    /**
     * m阶
     */
    protected int m = BPlusTree.m;

    /**
     * 键值
     */
    protected Integer[] keys;

    protected int keySize = 0;

    public abstract Pair<TreeNode, Integer> split();

    public abstract boolean isLeaf();

    public int keySize(){
        return keySize;
    }

    public Integer[] keys(){
        return keys;
    }

    public int m(){
        return m;
    }

}
