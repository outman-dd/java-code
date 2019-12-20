package code.algorithm.stringmatch;

/**
 * 〈BK算法〉<p>
 * O(n)
 *
 * @author zixiao
 * @date 2019/12/18
 */
public class RabinKarpMatcher implements IMatcher {

    @Override
    public boolean match(String string, String pattern) {
        int m = string.length();
        int n = pattern.length();
        if (m < n) {
            return false;
        }
        boolean match = false;
        for (int i = 0; i <= (m - n); i++) {
            String mString = string.substring(i, i + n);
            int mhash = mString.hashCode();
            int phash = pattern.hashCode();
            if (mhash == phash) {
                if (mString.equals(pattern)) {
                    match = true;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        IMatcher matcher = new RabinKarpMatcher();
        System.out.println(matcher.match("abcdef", "abcdefg"));
        System.out.println(matcher.match("abcdef", "abc"));
        System.out.println(matcher.match("abcdef", "cde"));
        System.out.println(matcher.match("abcdef", "def"));
        System.out.println(matcher.match("abcdef", "abd"));
        System.out.println(matcher.match("abcdef", "bde"));
        System.out.println(matcher.match("abcdef", "abcdef"));
    }
}
