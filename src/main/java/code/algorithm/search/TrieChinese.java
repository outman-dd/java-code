package code.algorithm.search;

import lombok.Data;

import java.util.*;

/**
 * 〈Trie树〉<p>
 * 1 构建Trie树的过程，需要扫描所有的字符串，时间复杂度是O(n)(n表示所有字符串的长度和)
 * 2 查找字符串的时间复杂度是O(k)，k表示要查找的字符串的长度
 *
 * @author zixiao
 * @date 2019/12/19
 */
public class TrieChinese {

    @Data
    class TrieNode {

        char data;

        Map<Character, TrieNode> children = new HashMap<>();

        boolean endChar = false;

        public TrieNode(char data) {
            this.data = data;
        }

    }

    private TrieNode root = new TrieNode('/');

    public void insert(String text) {
        char[] chars = text.toCharArray();
        TrieNode node = root;
        for (char c : chars) {
            TrieNode child = node.children.get(c);
            if (child == null) {
                child = new TrieNode(c);
                node.children.put(c, child);
            }
            node = child;
        }
        node.setEndChar(true);
    }

    public boolean find(String text) {
        char[] chars = text.toCharArray();
        TrieNode node = root;
        for (char c : chars) {
            TrieNode child = node.children.get(c);
            if (child == null) {
                return false;
            }
            node = child;
        }
        // 所有字符都在trie树上找到，判断最后一个是否为trie树上的叶子节点
        return (node.isEndChar());
    }

    /**
     * 搜索关键词提示
     *
     * @param text
     * @return
     */
    public List<String> findSimilar(String text) {
        char[] chars = text.toCharArray();
        TrieNode node = root;
        for (char c : chars) {
            TrieNode child = node.children.get(c);
            if (child == null) {
                return Collections.emptyList();
            }
            node = child;
        }

        List<String> similarList = new ArrayList<>();
        if (node.isEndChar()) {
            similarList.add(text);
        }
        findSimilar(node, text, similarList);
        return similarList;
    }

    private void findSimilar(TrieNode node, String currentStr, List<String> similarList) {
        for (TrieNode child : node.children.values()) {
            if (child == null) {
                continue;
            }
            String newStr = currentStr + child.data;
            if (child.isEndChar()) {
                similarList.add(newStr);
            }
            findSimilar(child, newStr, similarList);
        }
    }

    /**
     * 敏感词过滤
     * 1、敏感词建立trie数
     * 2、三个指针，p1指向root，p3指向第一个开始匹配敏感字，p2指向当前参与匹配的字
     * @param text
     * @return
     * @see https://www.cnblogs.com/kubidemanong/p/10834993.html
     */
    public String filterSensitiveWord(String text){
        char[] textArray = text.toCharArray();
        TrieNode p1 = root;
        int p2 = 0;
        int p3 = 0;

        while (p2 < textArray.length){
            TrieNode next = p1.children.get(textArray[p2]);
            if (next != null) {
                if (next.endChar) {
                    //匹配到敏感词，本次结束
                    System.out.println(String.format(">>find '%s', [%d, %d]", text.substring(p3, p2 + 1), p3, p2));
                    filter(textArray, p3, p2);

                    //从下一个位置重新开始
                    p2++;
                    p3 = p2;
                    p1 = root;
                } else {
                    //继续匹配下个字符
                    p1 = next;
                    p2++;
                }
            } else {
                p3++;
                p2 = p3;
                p1 = root;
            }
        }
        return new String(textArray);
    }

    private void filter(char[] chars, int from, int to){
        for (int i = from; i <= to; i++) {
            chars[i] = '*';
        }
    }

    public static void main(String[] args) {
        TrieChinese trie = new TrieChinese();
        //how，hi，her，hello，so，see
        trie.insert("学");
        trie.insert("中");
        trie.insert("中国");
        trie.insert("中午");
        trie.insert("历");
        trie.insert("历史书");
        trie.insert("中国人");
        trie.insert("中式快餐");

        System.out.println("**************** 查找词 ******************");
        System.out.println("学 :" + trie.find("学"));
        System.out.println("中国 :" + trie.find("中国"));
        System.out.println("中概股 :" + trie.find("中概股"));

        System.out.println("**************** 前缀匹配 ******************");
        System.out.println(">>> 中:");
        trie.findSimilar("中").forEach(s -> {
            System.out.println(s);
        });

        System.out.println(">>> 中国:");
        trie.findSimilar("中国").forEach(s -> {
            System.out.println(s);
        });

        System.out.println(">>> 历史:");
        trie.findSimilar("历史").forEach(s -> {
            System.out.println(s);
        });

        System.out.println("**************** 敏感词过滤 ******************");
        trie = new TrieChinese();
        trie.insert("艹");
        trie.insert("操你妈");
        trie.insert("妈逼");
        trie.insert("狗日");
        trie.insert("狗日");
        trie.insert("妈了个逼");

        System.out.println(trie.filterSensitiveWord("我去你妈了个逼"));
        System.out.println(trie.filterSensitiveWord("我艹他码，狗日的见鬼了"));
        System.out.println(trie.filterSensitiveWord("我们操场见"));

        //文本 abcdefghi ,以及三个敏感词"de", "bca", "bcf"
        trie = new TrieChinese();
        trie.insert("de");
        trie.insert("bca");
        trie.insert("bcf");
        System.out.println(trie.filterSensitiveWord("abcdefghi"));

    }
}
