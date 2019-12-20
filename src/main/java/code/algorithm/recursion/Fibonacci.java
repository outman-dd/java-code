package code.algorithm.recursion;

/**
 * 〈斐波那契数列〉<p>
 * n等于n-1与n-2之和
 * 1 1 2 3 5 8 13...
 * 递归公式：f(n) = f(n-1) + f(n-2), 终止条件：f(1) = 1, f(2) = 1
 *
 * @author zixiao
 * @date 2019/11/27
 */
public class Fibonacci {

    public static int f(int n) {
        if (n == 1 || n == 2) {
            return 1;
        }
        return f(n - 1) + f(n - 2);
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.print(f(i) + " ");
        }
    }

}
