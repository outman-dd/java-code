package code.jvm.oom;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈年轻代溢出〉<p>
 * 〈功能详细描述〉
 * -Xms32m -Xmx32m -Xmn16m -XX:PermSize=4m
 * -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/Users/zixiao/dump/
 *
 * @author zixiao
 * @date 18/3/2
 */
public class HeapNewOom {

    public static void main(String[] args) throws InterruptedException {
        oom();
    }

    public static void oom() throws InterruptedException {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int batchSize = 10000;
        for(int i=0; i< 50; i++){
            for(int j=0; j<batchSize; j++){
                int key = j+i*batchSize;
                map.put(key, key);
            }
            Thread.sleep(50L);
        }
        System.out.println("success");
    }

}
