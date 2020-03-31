package code.algorithm.leetcode;

import org.springframework.util.Assert;

/**
 * 〈零钱兑换〉<p>
 * 〈功能详细描述〉
 *  有1,2,5面值的硬币，现在有amount元，想兑换成硬币，最少需要多少枚硬币？
 *  递推方程： dp[i] = min(dp[i-1]+1, dp[i-2]+1, dp[i-5]+1)
 *
 * @author zixiao
 * @date 2020/1/17
 */
public class P322 {

    public int coinChange(int[] coins, int amount){
        int len = amount+1;
        int[] dp = new int[len];
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            int min = len;
            for (int coin : coins){
                if(i>=coin && dp[i-coin] != -1){
                    min = Math.min(min, dp[i-coin]+1);
                }
            }
            dp[i] = min == len ? -1 : min;
        }
        return dp[amount];
    }

    public static void main(String[] args) {
        P322 p322 = new P322();
        System.out.println(p322.coinChange(new int[]{2}, 3));

        System.out.println(p322.coinChange(new int[]{1, 2, 5}, 5));
        System.out.println(p322.coinChange(new int[]{1, 2, 5}, 9));
        Assert.isTrue(p322.coinChange(new int[]{186,419,83,408}, 6249)==20, "");

        //System.out.println(p322.coinChange(new int[]{2}, Integer.MAX_VALUE));
    }

}
