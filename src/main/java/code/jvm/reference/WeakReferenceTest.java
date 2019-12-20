package code.jvm.reference;

import org.junit.Test;

import java.lang.ref.WeakReference;

/**
 * 〈弱引用在下次YoungGC时回收〉<p>
 * -Xms20m -Xmx20m -XX:+PrintGCDetails
 *
 * @author zixiao
 * @date 2019/8/6
 */
public class WeakReferenceTest {

    /**
     * 在下次YGC时，回收
     */
    @Test
    public void weak(){
        House seller = new House();
        WeakReference<House> buyer = new WeakReference<House>(seller);
        seller = null;

        int i = 0;
        while (true){
            i++;
            if(buyer.get() == null){
                System.out.println("YGC时，弱引用对象被回收，"+ i);
                break;
            }else{
                System.out.println("still here, i="+i);
            }
        }
    }
}
