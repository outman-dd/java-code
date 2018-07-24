package code.spi;

/**
 * 〈Words〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/7/12
 */
public enum Words {

    HELLO("hello","你好"),
    BYE("see you","再见"),
    SORRY("sorry","对不起");

    private String en;

    private String cn;

    Words (String en, String cn){
        this.en = en;
        this.cn = cn;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }
}
