package code.algorithm.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 〈一致性hash〉<p>
 * 单调性：新增节点，数据只会映射到原节点或者新节点，不会映射到原其他节点
 * ﻿平衡性：数据尽可能分布均衡
 * 分散性：﻿当终端希望通过哈希过程将内容映射到缓冲上时，由于不同终端所见的缓冲范围有可能不同，从而导致哈希的结果不一致，最终的结果是相同的内容被不同的终端映射到不同的缓冲区中
 *
 * 1、常用的 hash%N 算法，那么在有机器添加或者删除后，很多原有的数据就无法找到了，这样严重的违反了单调性原则
 * 2、一致性hash通过增加虚拟节点，实现平衡性
 *
 * @author zixiao
 * @date 2019/3/22
 */
public class ConsistentHash<Node> {

    private List<Node> nodes;

    private TreeMap<Long, Node> vNodeMap;

    /**
     * 每个node对应的虚节点数
     */
    private int vNodeNum;


    public ConsistentHash(List<Node> nodes, int vNodeNum) {
        this.nodes = nodes;
        this.vNodeNum = vNodeNum;
        init();
    }

    private void init(){
        vNodeMap = new TreeMap();

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            for (int v =0; v<vNodeNum; v++){
                String key = node.toString()+"-"+v;
                vNodeMap.put(hash(key), node);
            }
        }
    }

    public Node getNode(String key){
        SortedMap<Long, Node> tailMap = vNodeMap.tailMap(hash(key));
        if(tailMap.size() > 0){
            return tailMap.get(tailMap.firstKey());
        }
        return vNodeMap.get(vNodeMap.firstKey());
    }


    /**
     *  MurMurHash算法，是非加密HASH算法，性能很高，
     *  比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免）
     *  等HASH算法要快很多，而且据说这个算法的碰撞率很低.
     *  http://murmurhash.googlepages.com/
     */
    private Long hash(String key) {

        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
             k = buf.getLong();

             k *= m;
             k ^= k >>> r;
             k *= m;

             h ^= k;
             h *= m;
            }

        if (buf.remaining() > 0) {
             ByteBuffer finish = ByteBuffer.allocate(8).order(
                      ByteOrder.LITTLE_ENDIAN);
             // for big-endian version, do this first:
             // finish.position(8-buf.remaining());
             finish.put(buf).rewind();
             h ^= finish.getLong();
             h *= m;
            }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }
}
