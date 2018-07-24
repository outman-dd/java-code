package code.spi;

/**
 * 〈中文扬声器〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/7/12
 */
public class ChineseSpeaker implements Speaker{

    @Override
    public void say(Words word) {
        System.out.println("说: "+word.getCn());
    }
}
