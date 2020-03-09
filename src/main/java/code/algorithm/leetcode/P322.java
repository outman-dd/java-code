package code.algorithm.leetcode;

import java.util.Arrays;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/17
 */
public class P322 {

    public int coinChange(int[] coins, int amount) {
        if(coins.length == 0 || amount == 0){
            return -1;
        }
        int maxIdx = coins.length-1;
        Arrays.sort(coins);
        int i = maxIdx;

        int num = 0;

        num += amount/coins[i];
        amount = amount%coins[i];

        return num;
    }

}
