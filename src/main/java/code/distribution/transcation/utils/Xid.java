package code.distribution.transcation.utils;

import code.util.NetworkUtils;

import java.util.UUID;

/**
 * 〈Xid = IP + PORT + TxId〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class Xid {

    private static String IP_PORT;

    static {
        System.out.println("NetworkUtils.getLocalHostIp");
        String ip = NetworkUtils.getLocalHostIp();
        IP_PORT = ip + ":" + "1234";
    }

    public static String newXid(){
        return IP_PORT +  ":" + UUID.randomUUID().toString().split("-")[0];
    }
}
