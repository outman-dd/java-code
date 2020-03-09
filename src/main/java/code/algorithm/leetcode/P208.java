package code.algorithm.leetcode;

/**
 * 〈实现 Trie (前缀树)〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2020/1/17
 */
public class P208 {

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

    /** Initialize your data structure here. */
    public P208() {

    }

    /** Inserts a word into the trie. */
    public void insert(String word) {
        char[] chars = word.toCharArray();
        TrieNode node = root;
        for (char c : chars) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode(c);
            }
            node = node.children[idx];
        }
        node.endChar = true;
    }

    /** Returns if the word is in the trie. */
    public boolean search(String word) {
        char[] chars = word.toCharArray();
        TrieNode node = root;
        for (char c : chars) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                return false;
            }
            node = node.children[idx];
        }
        // 所有字符都在trie树上找到，判断最后一个是否为trie树上的叶子节点
        return node.endChar;
    }

    /** Returns if there is any word in the trie that starts with the given prefix. */
    public boolean startsWith(String prefix) {
        char[] chars = prefix.toCharArray();
        TrieNode node = root;
        for (char c : chars) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                return false;
            }
            node = node.children[idx];
        }

        return true;
    }

}
