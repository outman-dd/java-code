package code.algorithm.leetcode;

/**
 * 〈121 买卖股票的最佳时机〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/17
 */
public class P121 {

    public int maxProfit(int[] prices) {
        if(prices.length <= 1){
            return 0;
        }
        int maxProfit = 0;
        int small = prices[0];
        for (int i = 1; i < prices.length; i++) {
            int current = prices[i];
            if(current > small){
                maxProfit = Math.max(maxProfit, current-small);
            }else if(current < small){
                small = current;
            }
        }
        return maxProfit;
    }


    public static void main(String[] args) {
        P121 p121 = new P121();
        int[] prices = {7,1,5,3,6,4};
        System.out.println(p121.maxProfit(prices)); //5

        prices = new int[]{7, 6, 4, 3, 1};
        System.out.println(p121.maxProfit(prices)); //0
    }
}
