package code.jvm;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈MinorGC〉<p>
 * -Xms32m -Xmx32m -Xmn16m -XX:PermSize=4m
 * -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xloggc:/Users/zixiao/dump/heap_trace.txt
 * -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/zixiao/dump/
 *
 *
 * heap_trace.txt:
 *
 0.719: [GC 0.723: [ParNew: 13184K->464K(14784K), 0.0034607 secs] 13184K->464K(31168K), 0.0079148 secs] [Times: user=0.01 sys=0.00, real=0.01 secs]
 0.738: [GC 0.738: [ParNew: 13648K->544K(14784K), 0.0020862 secs] 13648K->544K(31168K), 0.0021833 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 0.756: [GC 0.756: [ParNew: 13728K->631K(14784K), 0.0018353 secs] 13728K->631K(31168K), 0.0019221 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 Heap
 par new generation   total 14784K, used 6639K [7f8e00000, 7f9e00000, 7f9e00000)
 eden space 13184K,  45% used [7f8e00000, 7f93de1f0, 7f9ae0000)
 from space 1600K,  39% used [7f9c70000, 7f9d0dc10, 7f9e00000)
 to   space 1600K,   0% used [7f9ae0000, 7f9ae0000, 7f9c70000)
 concurrent mark-sweep generation total 16384K, used 0K [7f9e00000, 7fae00000, 7fae00000)
 concurrent-mark-sweep perm gen total 4864K, used 4801K [7fae00000, 7fb2c0000, 800000000)
 *
 * @author zixiao
 * @date 18/3/2
 */
public class MinorGc {

    public static void main(String[] args) throws InterruptedException {
        gc();
    }

    public static void gc() throws InterruptedException {
        Map<Integer, Integer> map = null;
        int batchSize = 100;
        for(int i=0; i< 5000; i++){
            map = new HashMap<Integer, Integer>();
            for(int j=0; j<batchSize; j++){
                int key = j+i*batchSize;
                map.put(key, key);
            }
        }
        System.out.println("success");
    }
}
