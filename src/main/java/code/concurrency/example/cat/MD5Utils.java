package code.concurrency.example.cat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 〈MD5工具类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/12
 */
public class MD5Utils {

    private static final char hexDigits[] = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private final static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>();

    public static MessageDigest getInstance() throws NoSuchAlgorithmException {
        if(MD5.get() == null){
            MD5.set(MessageDigest.getInstance("MD5"));
        }
        return MD5.get();
    }

    public static String md5(String key) throws NoSuchAlgorithmException {
        byte[] btInput = key.getBytes();
        // 获得MD5摘要算法的 MessageDigest 对象
        MessageDigest mdInst = getInstance();
        // 使用指定的字节更新摘要
        mdInst.update(btInput);
        // 获得密文
        byte[] md = mdInst.digest();
        // 把密文转换成十六进制的字符串形式
        int j = md.length;
        char str[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
            str[k++] = hexDigits[byte0 & 0xf];
        }
        return new String(str);
    }
}