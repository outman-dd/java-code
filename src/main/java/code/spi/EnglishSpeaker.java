package code.spi;

/**
 * 〈英文扬声器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/7/12
 */
public class EnglishSpeaker implements Speaker{

    @Override
    public void say(Words word) {
        System.out.println("Say: "+word.getEn());
    }
}
