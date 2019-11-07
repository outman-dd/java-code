package code.netty;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/10/16
 */
public class Server {

    public static void main(String[] args) throws Exception {
        new NettyServer().start(2345, "127.0.0.1");
    }
}
