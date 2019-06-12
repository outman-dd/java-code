package code.spi.extension;

import code.serialize.ISerializer;
import code.serialize.ProtostuffSerializer;
import code.spi.Words;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void test() throws InterruptedException {
        int threads = 20;
        CountDownLatch latch = new CountDownLatch(threads);
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        for (int i=0; i<threads; i++){
            executorService.execute(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(System.nanoTime());
                ISerializer serializer = extensionLoader.find("json");
                byte[] bytes = serializer.serialize(Words.HELLO);
                System.out.println(serializer.deserialize(bytes, Words.class));
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }
}
