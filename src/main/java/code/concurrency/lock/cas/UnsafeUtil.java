package code.concurrency.lock.cas;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * 〈UnsafeUtil〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/14
 */
public class UnsafeUtil {

    public static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe)field.get(null);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
