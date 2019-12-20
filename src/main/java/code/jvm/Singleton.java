package code.jvm;

/**
 * 加载一个类时，其内部静态类不会同时被加载。
 * 一个类被加载（线程安全），当且仅当其某个静态成员（静态域、构造器、静态方法等）被调用时发生。
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/10/9
 */
public class Singleton {

    private Singleton(){
        System.out.println("Singleton init");
    }

    public static class SingletonHolder{

        private static Singleton instance = new Singleton();

        static {
            System.out.println("Static Inner static code");
        }

        private static void innerTest(){
            System.out.println("innerTest");
        }
    }

    public static Singleton getInstance(){
        return SingletonHolder.instance;
    }

    public static void main(String[] args) {
        Singleton.getInstance();
    }
}
