package code.distribution.transcation.utils;

import java.util.UUID;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/25
 */
public class Xid {

    public static String newXid(){
        return UUID.randomUUID().toString().split("-")[0];
    }
}
