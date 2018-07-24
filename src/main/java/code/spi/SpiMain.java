package code.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 〈Java SPI〉<p>
 * 〈功能详细描述〉
 * META-INF\services文件下的code.spi.Speaker文件内容是服务类的全限命名：
   code.spi.EnglishSpeaker

 * @author zixiao
 * @date 18/7/12
 */
public class SpiMain {

    private static ServiceLoader<Speaker> serviceLoader = ServiceLoader.load(Speaker.class);

    public static void main(String[] args) {
        Iterator<Speaker> iterator = serviceLoader.iterator();
        while (iterator.hasNext()){
            iterator.next().say(Words.HELLO);
        }
    }
}
