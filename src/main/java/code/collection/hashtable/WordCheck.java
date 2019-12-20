package code.collection.hashtable;

/**
 * 〈单词拼写〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/6
 */
public class WordCheck {

    private HashMap<String, Byte> words = new HashMap<>(10);

    private byte v = 0x00;

    public void init(){
        words.put("hello", v);
        words.put("word", v);
        words.put("check", v);
        words.put("author", v);
        words.put("date", v);

        words.put("byte", v);
        words.put("put", v);
        words.put("public", v);
        words.put("void", v);
        words.put("class", v);

        words.put("private", v);
        words.put("window", v);
        words.put("sort", v);
        words.put("hash", v);
        words.put("java", v);
    }

    private boolean spellCheck(String word){
        return words.contains(word);
    }

    public static void main(String[] args) {
        WordCheck check = new WordCheck();
        check.init();

        System.out.println("'hash' spell ok? " + check.spellCheck("hash"));
        System.out.println("'aaa' spell ok? " + check.spellCheck("aaa"));

    }
}
