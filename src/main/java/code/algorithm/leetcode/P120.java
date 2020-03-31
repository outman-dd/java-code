package code.algorithm.leetcode;

import java.util.ArrayList;
import java.util.List;

public class P120 {

    public int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();
        int[][] dp = new int[n][n];

        //dp最后一行填充 三角形最后一行
        List<Integer> lastRow = triangle.get(n-1);
        for (int j = 0; j < n; j++) {
            dp[n-1][j] = lastRow.get(j);
        }

        //dp[i][j] = min(dp[i+1][j], dp[i+1][j]) + triangle[i+1][j+1]
        for (int i=n-2; i>=0; i--){
            for (int j = 0; j < i+1; j++) {
                dp[i][j] = Math.min(dp[i+1][j], dp[i+1][j+1]) + triangle.get(i).get(j);
            }
        }
        return dp[0][0];
    }

    public static void main(String[] args) {
        //     [2],
        //    [3,4],
        //   [6,5,7],
        //  [4,1,8,3]
        List<List<Integer>> triangle = new ArrayList<>();
        List<Integer> row = new ArrayList<>();
        row.add(2);
        triangle.add(row);

        row = new ArrayList<>();
        row.add(3);
        row.add(4);
        triangle.add(row);

        row = new ArrayList<>();
        row.add(6);
        row.add(5);
        row.add(7);
        triangle.add(row);

        row = new ArrayList<>();
        row.add(4);
        row.add(1);
        row.add(8);
        row.add(3);
        triangle.add(row);

        System.out.println(new P120().minimumTotal(triangle));
    }
}
