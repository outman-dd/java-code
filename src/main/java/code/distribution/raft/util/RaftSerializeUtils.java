package code.distribution.raft.util;

import code.serialize.HessianSerializer;
import code.serialize.ISerializer;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 2019-11-05
 */
public class RaftSerializeUtils {

    private static ISerializer serializer = new HessianSerializer();

    public static <T> byte[] serialize(T obj) {
        return serializer.serialize(obj);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return serializer.deserialize(bytes, clazz);
    }

}
