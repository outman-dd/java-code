package code.collection.tree.btree;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 叶子节点
 * B+树中的叶子节点跟内部结点是不一样的,
 * 叶子节点存储的是值，而非区间。
 * 这个定义里，每个叶子节点存储3个数据行的键值及地址信息。
 * k值是事先计算得到的，计算的依据是让所有信息的大小正好等于页的大小:
 * PAGE_SIZE = k*4*[keywords大小] + k*8*[dataAddress大小] + 8*[prev大小] + 8*[next大小]
 *
 * @author zixiao
 * @date 2019/12/20
 */
@Getter
public class LeafNode extends TreeNode{

    /**
     * 数据地址
     */
    private Long[] dataAddr;

    private LeafNode prev;

    private LeafNode next;

    public LeafNode() {
        keys = new Integer[m];
        dataAddr = new Long[m];
    }

    public LeafNode(int key, long addr) {
        this();
        insert(key, addr);
    }

    public boolean insert(int key, long addr){
        keys[keySize] = key;
        dataAddr[keySize] = addr;
        keySize++;
        return true;
    }

    public boolean delete(int idx){
        keys[idx] = null;
        dataAddr[idx] = null;
        keySize--;
        return true;
    }

    /**
     * 叶子结点分裂成左右两个叶子结点，
     * 左叶子结点包含前m/2个记录，
     * 右结点包含剩下的记录，
     * 将第m/2+1个记录的key进位到父结点中
     * @return 右结点和第（m/2+1）个记录的key
     */
    public Pair<TreeNode, Integer> split(){
        LeafNode newNode = new LeafNode();
        // this <-> B => this <-> newNode <-> B
        newNode.next = this.next;
        if(newNode.next != null){
            newNode.next.prev = newNode;
        }
        this.next = newNode;
        newNode.prev = this;
        int indexKey = keys[m/2];
        for (int i = m/2; i < keys.length; i++) {
            newNode.insert(keys[i], dataAddr[i]);
            delete(i);
        }
        return Pair.of(newNode, indexKey);
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
