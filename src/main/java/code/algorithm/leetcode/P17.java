package code.algorithm.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/3/3
 */
public class P17 {

    private static Map<Character, char[]> digitDict = new HashMap<>();

    static {
        digitDict.put('2', new char[]{'a', 'b', 'c'});
        digitDict.put('3', new char[]{'d', 'e', 'f'});
        digitDict.put('4', new char[]{'g', 'h', 'i'});
        digitDict.put('5', new char[]{'j', 'k', 'l'});
        digitDict.put('6', new char[]{'m', 'n', 'o'});
        digitDict.put('7', new char[]{'p', 'q', 'r', 's'});
        digitDict.put('8', new char[]{'t', 'u', 'v'});
        digitDict.put('9', new char[]{'w', 'x', 'y', 'z'});
    }

    public List<String> letterCombinations(String digits) {
        List<String> left = new ArrayList<>();
        for (char c : digits.toCharArray()) {
            left = join(left, digitDict.get(c));
        }
        return left;
    }

    private List<String> join(List<String> left, char[] right){
        List<String> strings = null;
        if(left.size() == 0){
            strings = new ArrayList<>(right.length);
            for (char c : right) {
                strings.add(""+c);
            }
        }else{
            strings = new ArrayList<>(left.size() * right.length);
            for (String s : left) {
                for (char c : right) {
                    strings.add(s+c);
                }
            }
        }
        return strings;
    }

    public static void main(String[] args) {
        P17 solution = new P17();
        for (String s : solution.letterCombinations("237")) {
            System.out.println(s);
        }
    }
}
