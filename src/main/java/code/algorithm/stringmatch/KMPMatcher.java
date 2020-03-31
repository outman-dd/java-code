package code.algorithm.stringmatch;

/**
 * 〈KMP算法〉<p>
 *  Knuth-Morris-Pratt 字符串查找算法，简称为 KMP算法，
 *  常用于在一个文本串 S 内查找一个模式串 P 的出现位置。
 * 时间复杂度：O(m+n)
 *
 * @author zixiao
 * @date 2020/1/17
 * @see https://zhuanlan.zhihu.com/p/76348091
 */
public class KMPMatcher implements IMatcher {

    @Override
    public boolean match(String string, String pattern) {
        char[] sArray = string.toCharArray();
        char[] pArray = pattern.toCharArray();

        //1 获取P串的next数组
        int[] nextArray = getNextArray(pArray);

        //比较次数
        int compare = 0;
        //移动次数
        int move = 0;
        //S字符串当前指针
        int curS = 0;
        //P模式串当前指针
        int curP = 0;
        //此位置，P串最后一个字符和S串最后一个字符对齐
        int maxCurS = sArray.length - pArray.length;

        //2 移动比较
        while (curS < sArray.length && curP < pArray.length) {
            if (sArray[curS] == pArray[curP]) {
                curP++;
                curS++;
            } else {
                if (curS >= maxCurS) {
                    break;
                }
                //移动到nextArray[curP]位置
                curP = nextArray[curP];
                //curP指针为-1时
                if (curP == -1) {
                    //则curP前移1个到为非负数位置
                    curP++;
                    //curS也前移1个
                    curS++;
                }
                move++;
            }
            compare++;
        }
        boolean match = curP == pArray.length;
        System.out.println("Match: " + match + ", compare: " + compare + ", move: " + move);
        return match;
    }

    private int[] getNextArray(char[] pArray) {
        int pLen = pArray.length;
        //1 P串所有子字符串的前缀后缀公共元素的最大长度
        int[] maxArray = new int[pLen];
        for (int i = 0; i < pLen; i++) {
            maxArray[i] = getMaxOfPrefixPostfix(pArray, i);
        }

        //2 next 数组相当于“最大长度值” 整体向右移动一位，然后初始值赋为-1。
        int[] nextArray = new int[pLen];
        System.arraycopy(maxArray, 0, nextArray, 1, pLen - 1);
        nextArray[0] = -1;

        return nextArray;
    }

    /**
     * 前缀后缀的公共元素的最大长度
     * 前缀 指除了最后一个字符以外，一个字符串的全部头部组合；
     * 后缀 指除了第一个字符以外，一个字符串的全部尾部组合。
     *
     * @return
     */
    private int getMaxOfPrefixPostfix(char[] pArray, int lastIdx) {
        if (lastIdx == 0) {
            return 0;
        }
        int max = 0;
        for (int i = 0; i < lastIdx; i++) {
            if (isEqual(pArray, i, lastIdx - i)) {
                max = Math.max(max, i + 1);
            }
        }
        return max;
    }

    private boolean isEqual(char[] pArray, int preLast, int postFirst) {
        for (int i = 0, j = postFirst; i <= preLast; i++, j++) {
            if (pArray[i] != pArray[j]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        //文本串S
        String s = "acabaabaabcaccaabc";
        //模式串P
        String p = "abaabcac";
        IMatcher matcher = new KMPMatcher();
        System.out.println(matcher.match(s, p));

        System.out.println(matcher.match("aaacaaabcacaddaaaab", "aaab"));
    }

}
