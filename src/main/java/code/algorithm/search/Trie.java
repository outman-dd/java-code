package code.algorithm.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 〈Trie树〉<p>
 * 1 构建Trie树的过程，需要扫描所有的字符串，时间复杂度是O(n)(n表示所有字符串的长度和)
 * 2 查找字符串的时间复杂度是O(k)，k表示要查找的字符串的长度
 *
 * @author zixiao
 * @date 2019/12/19
 */
public class Trie {

    @Data
    class TrieNode {
        char data;

        /**
         * 26个小写字母
         * 字符减'a'的ASCII码值的位置存储字符，如果不存在则null
         */
        TrieNode children[] = new TrieNode[26];

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
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode(c);
            }
            node = node.children[idx];
        }
        node.setEndChar(true);
    }

    public boolean find(String text) {
        char[] chars = text.toCharArray();
        TrieNode node = root;
        for (char c : chars) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                return false;
            }
            node = node.children[idx];
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
            int idx = c - 'a';
            if (node.children[idx] == null) {
                return Collections.emptyList();
            }
            node = node.children[idx];
        }

        List<String> similarList = new ArrayList<>();
        if (node.isEndChar()) {
            similarList.add(text);
        }
        findSimilar(node, text, similarList);
        return similarList;
    }

    private void findSimilar(TrieNode node, String currentStr, List<String> similarList) {
        for (TrieNode child : node.children) {
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

    public static void main(String[] args) {
        Trie trie = new Trie();
        //how，hi，her，hello，so，see
        trie.insert("how");
        trie.insert("hi");
        trie.insert("her");
        trie.insert("hello");
        trie.insert("so");
        trie.insert("see");
        trie.insert("history");
        trie.insert("hist");

        System.out.println("so :" + trie.find("so"));
        System.out.println("hello :" + trie.find("hello"));
        System.out.println("he :" + trie.find("he"));
        System.out.println("his :" + trie.find("his"));

        System.out.println(">>> he:");
        trie.findSimilar("he").forEach(s -> {
            System.out.println(s);
        });

        System.out.println(">>> hi:");
        trie.findSimilar("hi").forEach(s -> {
            System.out.println(s);
        });
    }
}
