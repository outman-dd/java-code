package code.algorithm.leetcode;

/**
 * 给定一个字符串，验证它是否是回文串，只考虑字母和数字字符，可以忽略字母的大小写。
 *
 * 说明：本题中，我们将空字符串定义为有效的回文串。
 *
 * @author zixiao
 * @date 2020/1/16
 */
public class P125 {

    public static boolean isHuiwen(char[] text) {
        if (text.length <= 1) {
            return true;
        }
        int l = 0;
        int r = text.length - 1;
        while (l < r) {
            if (!Character.isLetterOrDigit(text[l])) {
                l++;
            } else if (!Character.isLetterOrDigit(text[r])) {
                r--;
            } else {
                //都是数字或字符串时
                if (Character.toLowerCase(text[l]) == Character.toLowerCase(text[r])) {
                    l++;
                    r--;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPalindrome(String s) {
        if (s.length() <= 1) {
            return true;
        }
        int l = 0;
        int r = s.length() - 1;
        while (l < r) {
            if (!Character.isLetterOrDigit(s.charAt(l))) {
                l++;
            } else if (!Character.isLetterOrDigit(s.charAt(r))) {
                r--;
            } else {
                //都是数字或字符串时
                if (Character.toLowerCase(s.charAt(l)) == Character.toLowerCase(s.charAt(r))) {
                    l++;
                    r--;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String s = "I m a mi";
        System.out.println(s + ":" + isHuiwen(s.toCharArray()));

        s = "i";
        System.out.println(s + ":" + isHuiwen(s.toCharArray()));

        s = "i a ai";
        System.out.println(s + ":" + isHuiwen(s.toCharArray()));

        s = "hello";
        System.out.println(s + ":" + isHuiwen(s.toCharArray()));
    }
}
