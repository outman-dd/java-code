package code.algorithm.leetcode;

import java.util.Stack;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/19
 */
public class P394 {

    public String decodeString(String s) {
        Stack<String> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == ']') {
                compose(stack);
            } else {
                stack.push(s.substring(i, i + 1));
            }
        }

        StringBuilder sb = new StringBuilder();
        stack.forEach(c -> sb.append(c));
        return sb.toString();
    }

    private void compose(Stack<String> stack) {
        String str = "";
        String num = "";
        // [] 中间的字符串
        while (!stack.isEmpty()) {
            String s = stack.pop();
            if (s.equals("[")) {
                // [ 之前的数字
                while (!stack.isEmpty()) {
                    s = stack.peek();
                    if (Character.isDigit(s.charAt(0))) {
                        stack.pop();
                        num = s + num;
                    } else {
                        break;
                    }
                }
                break;
            } else {
                str = s + str;
            }
        }

        int n = Integer.parseInt(num);
        for (int i = 0; i < n; i++) {
            stack.push(str);
        }
    }

    public String decodeString_bak(String s) {
        char[] chars = s.toCharArray();
        Stack<Character> charStack = new Stack<>();
        for (char c : chars) {
            if (c == ']') {
                compose_bak(charStack);
            } else {
                charStack.push(c);
            }
        }

        StringBuilder sb = new StringBuilder();
        charStack.forEach(c -> sb.append(c));
        return sb.toString();
    }

    private void compose_bak(Stack<Character> charStack) {
        String str = "";
        String num = "";
        while (!charStack.isEmpty()) {
            char c = charStack.pop();
            if (Character.isLetter(c)) {
                str = c + str;
            } else if (c == '[') {
                while (!charStack.isEmpty()) {
                    c = charStack.peek();
                    if (Character.isDigit(c)) {
                        charStack.pop();
                        num = c + num;
                    } else {
                        break;
                    }
                }
                break;
            }
        }

        int n = Integer.parseInt(num);
        char[] strArray = str.toCharArray();
        for (int i = 0; i < n; i++) {
            for (char c : strArray) {
                charStack.push(c);
            }
        }
    }

    public static void main(String[] args) {
        P394 p = new P394();

        String s = "2[a2[b1[cf]]]3[d]";
        System.out.println(p.decodeString(s));
        System.out.println(p.decodeString(s));

    }

}
