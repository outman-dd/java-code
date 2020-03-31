package code.algorithm.dp;

/**
 * 爬楼梯，每次走1或2级，爬完n几台阶 有几种方法
 * 递推方程：dp[i] = dp[i-1] + dp[i-2]
 */
public class StairDp {

    public int f(int n){
        int dp[] = new int[n+1];
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 2;

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i-1] + dp[i-2];
        }
        return dp[n];
    }

    public static void main(String[] args) {
        //89
        System.out.println(new StairDp().f(10));
    }
}
