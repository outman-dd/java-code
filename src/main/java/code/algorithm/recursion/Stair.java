package code.algorithm.recursion;

/**
 * 〈台阶问题〉<p>
 * n级台阶，每次走1步或2步 有多少种走法
 * 第1次走1步，剩下n-1步有f(n-1)种走法；第1次走2步，剩下n-2步有f(n-2)种走法
 * 所以f(n) = f(n-1) + f(n-2)
 * 终止条件：f(1) = 1, f(2) = 2
 *
 * @author zixiao
 * @date 2019/11/27
 */
public class Stair {

    public static int f(int n){
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        return f(n - 1) + f(n - 2);
    }

    public static int f2(int n) {
        int sum = 1;
        int n_2 = 0;
        int n_1 = 1;
        //f(n) = f(n-2)+f(n-1), f(0) = 1
        for(int i=0; i<n; i++){
            sum = (n_2 + n_1)%1000000007;
            n_2 = n_1;
            n_1 = sum;
        }
        return sum;
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 11; i++) {
            System.out.println(i + " stairs :" + f(i));
        }
        for (int i = 1; i <= 11; i++) {
            System.out.println(i + " stairs :" + f2(i));
        }
    }
}
