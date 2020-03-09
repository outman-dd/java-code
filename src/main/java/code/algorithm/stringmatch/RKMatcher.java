package code.algorithm.stringmatch;

/**
 * 〈BK算法〉<p>
 * 时间复杂度：O(n)
 * Rabin-Karp算法的思想：
 *
 * 1 假设子串的长度为M,目标字符串的长度为N
 * 2 计算子串的hash值
 * 3 计算目标字符串中每个长度为M的子串的hash值（共需要计算N-M+1次）
 * 4 比较hash值
 *   如果hash值不同，字符串必然不匹配，
 *   如果hash值相同，还需要使用朴素算法再次判断
 *
 * @author zixiao
 * @date 2019/12/18
 */
public class RKMatcher implements IMatcher {

    @Override
    public boolean match(String string, String pattern) {
        int m = string.length();
        int n = pattern.length();
        if (m < n) {
            return false;
        }
        int phash = pattern.hashCode();
        for (int i = 0; i <= (m - n); i++) {
            String mString = string.substring(i, i + n);
            int mhash = mString.hashCode();
            if (mhash == phash) {
                if (mString.equals(pattern)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        IMatcher matcher = new RKMatcher();
        System.out.println(matcher.match("abcdef", "abcdefg"));//false
        System.out.println(matcher.match("abcdef", "abc"));//true
        System.out.println(matcher.match("abcdef", "cde"));//true
        System.out.println(matcher.match("abcdef", "def"));//true
        System.out.println(matcher.match("abcdef", "abd"));//false
        System.out.println(matcher.match("abcdef", "bde"));//false
        System.out.println(matcher.match("abcdef", "abcdef"));//true
    }
}
