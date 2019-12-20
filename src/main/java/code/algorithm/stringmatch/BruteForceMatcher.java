package code.algorithm.stringmatch;

/**
 * 〈BF算法〉<p>
 * O(n*m)
 *
 * @author zixiao
 * @date 2019/12/18
 */
public class BruteForceMatcher implements IMatcher {

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
        IMatcher matcher = new BruteForceMatcher();
        System.out.println(matcher.match("abcdef", "abcdefg"));
        System.out.println(matcher.match("abcdef", "abc"));
        System.out.println(matcher.match("abcdef", "cde"));
        System.out.println(matcher.match("abcdef", "def"));
        System.out.println(matcher.match("abcdef", "abd"));
        System.out.println(matcher.match("abcdef", "bde"));
        System.out.println(matcher.match("abcdef", "abcdef"));
    }

}
