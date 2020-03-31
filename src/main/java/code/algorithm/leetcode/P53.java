package code.algorithm.leetcode;

public class P53 {

    public int maxSubArray(int[] nums){
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int n = nums.length;
        int dp[] = new int[n];
        dp[0] = nums[0];

        int max = dp[0];
        for (int i = 1; i < n; i++) {
            dp[i] = Math.max(dp[i-1]+nums[i], nums[i]);
            max = Math.max(max, dp[i]);
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(new P53().maxSubArray(new int[]{-2,1,-3,4,-1,2,1,-5,4}));
    }
}
