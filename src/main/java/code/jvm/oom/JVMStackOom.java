package code.jvm.oom;

/**
 * 〈虚拟机栈溢出〉<p>
 * 〈功能详细描述〉
 *
 * 1、-Xss128k
 * stack length = 401 时，发生StackOverflowError
 *
 * 2、-Xss256k
 * stack length = 10827 时，发生StackOverflowError
 *
 * @author zixiao
 * @date 18/3/2
 */
public class JVMStackOom {

    private int stackLength = 1;

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) throws Throwable {
        JVMStackOom oom = new JVMStackOom();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + oom.stackLength);
            throw e;
        }
    }
}
