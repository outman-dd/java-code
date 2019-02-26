package code.cache;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import java.nio.charset.Charset;

/**
 * 〈布隆过滤器〉<p>
 *  bloom算法类似一个hash set，用来判断某个元素（key）是否在某个集合中。
    算法：
    1. 首先需要k个hash函数，每个函数可以把key散列成为1个整数
    2. 初始化时，需要一个长度为n比特的数组，每个比特位初始化为0
    3. 某个key加入集合时，用k个hash函数计算出k个散列值，并把数组中对应的比特位置为1
    4. 判断某个key是否在集合时，用k个hash函数计算出k个散列值，并查询数组中对应的比特位，如果所有的比特位都是1，认为在集合中。

    优点：不需要存储key，节省空间
    缺点：
    1. 算法判断key在集合中时，有一定的概率key其实不在集合中
    2. 无法删除
 *
 * @author zixiao
 * @date 19/2/26
 */
public class BloomFilter<K> {

    private com.google.common.hash.BloomFilter<K> bloomFilter;

    public BloomFilter(long expectedInsertions, double falsePositiveProbability){
        bloomFilter = com.google.common.hash.BloomFilter.create(new Funnel<K>() {
            @Override
            public void funnel(K from, PrimitiveSink into) {
                into.putString(from.toString(), Charset.defaultCharset());
            }
        }, expectedInsertions, falsePositiveProbability);
    }

    boolean exist(K key){
        return bloomFilter.mightContain(key);
    }

    void add(K key){
        bloomFilter.put(key);
    }

    void remove(K key){
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        long size = 100000000;
        BloomFilter<String> bf = new BloomFilter<>(size, 0.001);
        for(long i=0; i<size; i++){
            bf.add(String.format("%09d", i));
        }
        int mistake = 0;
        int testCount = 1000000;
        for(long i=size; i<(size+testCount); i++){
            if(bf.exist(String.format("%09d", i))){
                mistake++;
            }
        }
        System.out.println("False Positive Probability:" + (double)mistake/testCount);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
