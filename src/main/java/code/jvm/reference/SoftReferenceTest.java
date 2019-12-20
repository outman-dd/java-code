package code.jvm.reference;

import org.junit.Test;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 〈软引用，在OOM前回收〉<p>
 * -Xms20m -Xmx20m
 *
 * @author zixiao
 * @date 2019/8/6
 */
public class SoftReferenceTest {

    /**
     * 360146次时, 发生OOM
     */
    @Test
    public void soft(){
        List<SoftReference> houses = new ArrayList<SoftReference>();
        int i = 0;
        while (true){
            i++;
            System.out.println(i+ ", size:" + houses.size());
            SoftReference<House> house = new SoftReference<House>(new House());
            houses.add(house);
        }
    }


    /**
     * 2423次时，发生OOM
     */
    @Test
    public void strong(){
        List<House> houses = new ArrayList<House>();
        int i = 0;
        while (true){
            i++;
            House house = new House();
            houses.add(house);
            System.out.println(i + ", size:" + houses.size());
        }
    }

}
