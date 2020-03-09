package code.algorithm.leetcode;

/**
 * 字符串原地反转，只花费O(1)的空间
 *
 * @author zixiao
 * @date 2020/1/16
 */
public class P344 {

    public static char[] reverse(char[] text){
        char temp;
        for (int i = 0; i < text.length; i++) {
            if(i >= (text.length-i-1)){
                break;
            }
            temp = text[i];
            text[i] = text[text.length-i-1];
            text[text.length-i-1] = temp;
        }
        return text;
    }

    public static void main(String[] args) {
        System.out.println(reverse("abc".toCharArray()));
        System.out.println(reverse("abcd".toCharArray()));
    }

}
