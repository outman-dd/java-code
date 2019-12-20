package code.util;

import java.security.MessageDigest;

import static com.alibaba.fastjson.util.IOUtils.UTF8;

/**
 * 〈加密工具类〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019/12/10
 */
public class Encrypt {

    public static String encrypt(String password, String slat) {
        return sha256(md5(password) + slat);
    }

    public static String sha256(String inputText) {
        try {
            MessageDigest m = MessageDigest.getInstance("SHA-256");
            m.update(inputText.getBytes(UTF8));
            byte s[] = m.digest();
            return hex(s);
        } catch (Exception e) {
            throw new IllegalArgumentException(inputText + "的SHA-256加密失败", e);
        }
    }

    public static String md5(String inputText) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(inputText.getBytes(UTF8));
            byte s[] = m.digest();
            return hex(s);
        } catch (Exception e) {
            throw new IllegalArgumentException(inputText + "的MD5加密失败", e);
        }
    }

    /**
     * 返回十六进制字符串
     *
     * @param arr
     * @return
     */
    private static String hex(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }
}
