package code.algorithm.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈和为s的连续正数序列〉<p>
 * 输入一个正整数 target ，输出所有和为 target 的连续正整数序列（至少含有两个数）。
 * 序列内的数字由小到大排列，不同序列按照首个数字从小到大排列。
 *
 * 示例 1：
 * 输入：target = 9
 * 输出：[[2,3,4],[4,5]]
 *
 * 示例 2：
 * 输入：target = 15
 * 输出：[[1,2,3,4,5],[4,5,6],[7,8]]
 *  
 * 限制：
 * 1 <= target <= 10^5
 *
 * 链接：https://leetcode-cn.com/problems/he-wei-sde-lian-xu-zheng-shu-xu-lie-lcof
 *
 * @author zixiao
 * @date 2020/3/6
 */
public class M57 {

    static class MN {
        int m;
        int n;

        public MN(int m, int n) {
            this.m = m;
            this.n = n;
        }
    }

    public int[][] findContinuousSequence(int target) {
        if (target <= 2 || target == 4) {
            return new int[0][];
        }
        int max = (target+1)/2;
        int l = 1;
        int r = 2;
        int sum = 0;
        for (; l<max; l++){
            sum = l;
            for(; r<=max; r++){

            }
        }


        return new int[0][];
    }

    public int[][] findContinuousSequence1(int target) {
        if (target <= 2 || target == 4) {
            return new int[0][];
        }

        List<MN> mnList = new ArrayList();
        int maxN = (target + 1) / 2;
        for (int m = 1; m < maxN; m++) {
            for (int n = m + 1; n <= maxN; n++) {
                if ((m + n) * (n - m + 1) / 2 == target) {
                    mnList.add(new MN(m, n));
                    break;
                }
            }
        }
        if (!mnList.isEmpty()) {
            int[][] result = new int[mnList.size()][];
            for (int i = 0; i < mnList.size(); i++) {
                MN mn = mnList.get(i);
                result[i] = new int[mn.n - mn.m + 1];
                for (int j = mn.m, k = 0; j <= mn.n; j++, k++) {
                    result[i][k] = j;
                }
            }
            return result;
        }
        return new int[0][];
    }

    public static void main(String[] args) {
        M57 m57 = new M57();
       // m57.findContinuousSequence(1);
        m57.findContinuousSequence(9);
    }

}
