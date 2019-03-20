package code.spi.extension;

import code.serialize.ISerializer;
import code.serialize.ProtostuffSerializer;
import code.spi.Words;

import org.junit.Test;
import org.springframework.util.Assert;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/3/18
 */
public class ExtensionTest {

    private static ExtensionLoader<ISerializer> extensionLoader = ExtensionLoader.load(ISerializer.class);

    @Test
    public void testDefault(){
        ISerializer serializer = extensionLoader.find("protostuff");
        Assert.isTrue(serializer instanceof ProtostuffSerializer, "");
        byte[] bytes = serializer.serialize(Words.HELLO);
        System.out.println(serializer.deserialize(bytes, Words.class));
    }

    @Test
    public void testFind(){
        ISerializer serializer = extensionLoader.get();
        byte[] bytes = serializer.serialize(Words.HELLO);
        System.out.println(serializer.deserialize(bytes, Words.class));
    }

}
