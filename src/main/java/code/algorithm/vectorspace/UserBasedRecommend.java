package code.algorithm.vectorspace;

import org.apache.commons.lang3.tuple.Pair;

/**
 * 〈相似用户推荐〉<p>
 * 找到跟你口味偏好相似的用户，把他们爱听的歌曲推荐给你;
 * <p>
 * 每个用户对所有歌曲的喜爱程度，都用一个向量表示。
 * 我们计算出两个向量之间的欧几里得距离(Euclidean distance)，作为两个用户的口味相似程度的度量。
 * 距离距离越小越相似，和经纬度类似
 *
 * @author zixiao
 * @date 2019/12/20
 */
public class UserBasedRecommend {

    private String[] users = {"你", "小明", "小王", "小红", "小李"};

    private String[] songs = {"安静", "晴天", "十年", "后来", "春天里"};

    private int[][] userFav = new int[users.length][songs.length];

    public UserBasedRecommend() {
        init();
    }

    /**
     * 喜好评分表
     * <p>
     * 人\歌 安静  晴天  十年  后来  春天里
     * 你    5    3     3    0    -1
     * 小明  4    5     2    1     0
     * 小王  1    0     5    5    -1
     * 小红  3    0     0    3     0
     * 小李  5    2     4    0     0
     */
    private void init() {
        userFav[0] = new int[]{5, 3, 3, 0, -1};
        userFav[1] = new int[]{4, 5, 2, 1, 0};
        userFav[2] = new int[]{1, 0, 5, 5, -1};
        userFav[3] = new int[]{3, 0, 0, 3, 0};
        userFav[4] = new int[]{5, 2, 4, 0, 0};
    }

    public String findSimilarUser(String userName) {
        int userId = -1;
        for (int i = 0; i < users.length; i++) {
            if (userName.equals(users[i])) {
                userId = i;
                break;
            }
        }
        int similarId = findSimilarUser(userId);
        return users[similarId];
    }

    private int findSimilarUser(int userId) {
        int[] favArray = userFav[userId];
        Pair<Integer, Double> min = null;
        for (int i = 0; i < userFav.length; i++) {
            if (i != userId) {
                double dis = euDistance(favArray, userFav[i]);
                if (min == null || dis < min.getValue()) {
                    min = Pair.of(i, dis);
                }
            }
        }
        System.out.println("最小欧式距离：" + min.getValue() + ", userId=" + min.getKey());
        return min.getKey();
    }

    private double euDistance(int[] array1, int[] array2) {
        int sum = 0;
        for (int i = 0; i < array1.length; i++) {
            int diff = (array1[i] - array2[i]);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    public static void main(String[] args) {
        UserBasedRecommend recommend = new UserBasedRecommend();
        System.out.println("相似用户：" + recommend.findSimilarUser("你"));
    }
}
