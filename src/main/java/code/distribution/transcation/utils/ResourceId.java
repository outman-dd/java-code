package code.distribution.transcation.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/2/26
 */
public class ResourceId {

    private static Map<String, String> map = new HashMap<String, String>();

    static {
        map.put("order", "jdbc:mysql://db.tbj.com:3308/order");

        map.put("pay", "jdbc:mysql://db.tbj.com:3308/pay");

    }

    public static String newResourceId(String tableName){
        String prefix = tableName.substring(0, tableName.indexOf("_"));
        return map.get(prefix);
    }

}
