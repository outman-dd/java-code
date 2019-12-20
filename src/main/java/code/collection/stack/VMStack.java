package code.collection.stack;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/11/26
 */
public class VMStack{

    public static void main(String[] args) {
        int a = 1;
        int b = 2;
        int ret = 0;
        ret = a + b;
        System.out.println("a+b="+ret);
    }

    private int add(int x, int y){
        int sum = 0;
        sum = x + y;
        return sum;
    }
}
