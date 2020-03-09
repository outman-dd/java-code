package code.algorithm.leetcode;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/3/5
 */
public class P1103 {

    public int[] distributeCandies(int candies, int num) {
        int[] ans = new int[num];

        int round = 0;
        int remain = candies;
        int num_x_num = num * num;
        int n_n_2 = (num_x_num + num) / 2;
        while (remain > 0) {
            int cost = round * num_x_num + n_n_2;
            if (remain >= cost) {
                remain -= cost;
                round++;
            } else {
                break;
            }
        }

        if (round == 0) {
            for (int i = 0; i < num; i++) {
                if (remain == 0) {
                    break;
                }
                ans[i] = Math.min(i + 1, remain);
                remain -= ans[i];
            }
            return ans;
        }

        int round_x_num = round * num;
        int rrn2 = (round - 1) * round_x_num / 2;
        for (int i = 0; i < num; i++) {
            ans[i] = rrn2 + (i + 1) * round;
        }

        if (remain > 0) {
            for (int i = 0; i < num; i++) {
                if (remain == 0) {
                    break;
                }
                int add = Math.min(round_x_num + i + 1, remain);
                ans[i] += add;
                remain -= add;
            }
        }

        return ans;
    }

    public static void main(String[] args) {
        P1103 solution = new P1103();
        for (int i : solution.distributeCandies(7, 4)) {
            System.out.print(i + ",");
        }
        System.out.println();

        for (int i : solution.distributeCandies(10, 3)) {
            System.out.print(i + ",");
        }
        System.out.println();

        for (int i : solution.distributeCandies(13, 3)) {
            System.out.print(i + ",");
        }
        System.out.println();

        System.out.println(6>>1);
    }

}
