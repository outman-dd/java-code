package code.algorithm.dp;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/3/6
 */
public class Coin {

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
        Coin coin = new Coin();
        coin.waysToChange(30);
    }



}
