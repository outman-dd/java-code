package code.serialize;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * 〈Json〉<p>
 *
 * @author zixiao
 * @date 16/9/28
 */
public class JsonSerializer implements ISerializer{

    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    @Override
    public <T> byte[] serialize(final T obj) {
        final String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    private static String toJson(final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    @Override
    public <T> T deserialize(final byte[] data, Class<T> classOfT) {
        final String json = new String(data, CHARSET_UTF8);
        return fromJson(json, classOfT);
    }

    private static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

}
