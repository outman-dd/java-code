package code.algorithm.leetcode;

/**
 * 〈编辑距离〉<p>
 * <p>
 * a 如果最上方的字符等于最左方的字符，则为左上方的数字, 否则为左上方的数字+1
 * b 左方数字+1
 * c 上方数字+1
 * 取 min(a,b,c)
 * <p>
 * [] [] c o f f e e
 * [] 0  1 2 3 4 5 6
 * c  1  0 1 2 3 4 5
 * a  2  1 1 2 3 4 5
 * f  3  2 2 2 3 4 5
 * e  4  3 3 3 3 3 3
 *
 * @author zixiao
 * @date 2020/3/3
 */
public class P72 {

    public int minDistance(String wordX, String wordY) {
        int xLen = wordX.length() + 1;
        int yLen = wordY.length() + 1;

        if (xLen * yLen == 0)
            return xLen + yLen;

        short dis[][] = new short[xLen][yLen];

        //初始化
        for (short i = 0; i < yLen; i++) {
            dis[0][i] = i;
        }
        for (short i = 0; i < xLen; i++) {
            dis[i][0] = i;
        }

        for (int x = 1; x < xLen; x++) {
            for (int y = 1; y < yLen; y++) {
                int a;
                if (wordX.charAt(x - 1) == wordY.charAt(y - 1)) {
                    a = dis[x - 1][y - 1];
                } else {
                    a = dis[x - 1][y - 1] + 1;
                }
                int b = dis[x][y - 1] + 1;
                int c = dis[x - 1][y] + 1;
                dis[x][y] = min(a, b, c);
            }
        }

        return dis[xLen - 1][yLen - 1];
    }

    private short min(int a, int b, int c) {
        return (short)Math.min(Math.min(a, b), c);
    }

    public static void main(String[] args) {
        P72 solution = new P72();
        System.out.println(solution.minDistance("horse", "ros"));
        System.out.println(solution.minDistance("intention", "execution"));
    }

}
