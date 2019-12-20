package code.algorithm.recursion;

/**
 * 〈阶乘〉<p>
 * 0!=1, 1!=1*0!, 2!=2*1!, 3!=3*2!, 4!=4*3!
 * 递归公式：f(n) = n * f(n-1), 终止条件：f(0) = 1
 *
 * @author zixiao
 * @date 2019/11/27
 */
public class Factorial {

    public static int f(int n) {
        if (n == 0) {
            return 1;
        }
        return n * f(n - 1);
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(i + "! = " + f(i));
        }
    }

}
