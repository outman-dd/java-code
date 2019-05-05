package code.concurrency.lock.cas;

import sun.misc.Unsafe;

import java.io.Serializable;

/**
 * 〈CAS实现原子int〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/13
 */
public class CasInteger implements Serializable{

    private static final Unsafe unsafe = UnsafeUtil.getUnsafe();

    /**
     * 获取对象某个属性的地址偏移值(相对对象起始内存地址)
     * 所以同一个类的多个对象，同一个属性objectFieldOffset的值相同
     */
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                    (CasInteger.class.getDeclaredField("value"));
            System.out.println("valueOffset="+valueOffset);
        } catch (Exception ex) { throw new Error(ex); }
    }


    private volatile int value;

    public CasInteger(int value) {
        this.value = value;
    }

    public CasInteger() {
        this.value = 0;
    }

    public boolean compareAndSet(int except, int newValue){
        return unsafe.compareAndSwapInt(this, valueOffset, except, newValue);
    }

    public static void main(String[] args) {
        CasInteger casInteger = new CasInteger();
        if(casInteger.compareAndSet(0, 1)){
            System.out.println("CAS success. value="+ casInteger.value);
        }
    }
}
