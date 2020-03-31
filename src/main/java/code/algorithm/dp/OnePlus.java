package code.algorithm.dp;

/**
 * 一般解决动态规划问题，分为四个步骤，分别是
 * 1 问题拆解，找到问题之间的具体联系
 * 2 状态定义
 * 3 递推方程推导
 * 4 实现
 *
 * n个1相加的结果： 1+1+1+1+1+1+1+1+1
 * 问题拆解: 9个1等于8个1+1， 8个1等于7个1+1 ...
 * 状态变化: 后一个问题的答案 = 前一个问题的答案 + 1，状态的每次变化就是 +1
 * 递推方程:  dp[i] = dp[i - 1] + 1
 */
public class OnePlus {

    public int calc(int n){
        int[] dp = new int[n+1];
        dp[0] = 0;

        for (int i = 1; i <= n; i++) {
            dp[i] = dp[i-1]+1;
        }
        return dp[n];
    }

    public static void main(String[] args) {
        System.out.println(new OnePlus().calc(8));
    }
}
