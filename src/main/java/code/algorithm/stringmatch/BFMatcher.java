package code.algorithm.stringmatch;

/**
 * 〈BF算法〉<p>
 * BF算法，即暴风(Brute Force)算法，是普通的模式匹配算法
 * 时间复杂度：O(n*m)
 * BF算法的思想就是将目标串S的第一个字符与模式串T的第一个字符进行匹配，
 *  若相等，则继续比较S的第二个字符和 T的第二个字符；
 *  若不相等，则比较S的第二个字符和T的第一个字符，依次比较下去，直到得出最后的匹配结果。
 *
 * @author zixiao
 * @date 2019/12/18
 */
public class BFMatcher implements IMatcher {

    @Override
    public boolean match(String string, String pattern) {
        char[] main = string.toCharArray();
        char[] sub = pattern.toCharArray();
        int m = main.length;
        int n = sub.length;
        if (m < n) {
            return false;
        }
        for (int i = 0; i <= (m - n); i++) {
            boolean match = true;
            for (int j = 0; j < n; j++) {
                if (sub[j] != main[i + j]) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        IMatcher matcher = new BFMatcher();
        System.out.println(matcher.match("abcdef", "abcdefg"));
        System.out.println(matcher.match("abcdef", "abc"));
        System.out.println(matcher.match("abcdef", "cde"));
        System.out.println(matcher.match("abcdef", "def"));
        System.out.println(matcher.match("abcdef", "abd"));
        System.out.println(matcher.match("abcdef", "bde"));
        System.out.println(matcher.match("abcdef", "abcdef"));
    }

}
