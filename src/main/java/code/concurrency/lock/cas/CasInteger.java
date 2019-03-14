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
