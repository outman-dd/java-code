package code.algorithm.dp;

/**
 * 〈钱币表示〉<p>
 * 有数量不限的硬币，币值为25分、10分、5分和1分，请编写代码计算n分有几种表示法。
 * 给定一个int n，请返回n分有几种表示法。保证n小于等于100000，为了防止溢出，请将答案Mod 1000000007。
 *
 *  地推方程：dp[i] += dp[i-1] + dp[i-5] + dp[i-10] + dp[i-25]
 *
 * @author zixiao
 * @date 2020/3/6
 */
public class Coins {

    public int waysToChange(int n) {
        int[] dp = new int[n+1];
        dp[0] = 1;
        int[] coins = new int[]{1, 5 ,10, 25};
        for(int coin : coins){
            for(int i=coin; i<=n; i++){
                dp[i] = (dp[i] + dp[i-coin])%1000000007;
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        Coins coin = new Coins();
        System.out.println(coin.waysToChange(30));
    }

}
