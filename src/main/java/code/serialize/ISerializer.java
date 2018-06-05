package code.serialize;

/**
 * 〈一句话功能简述〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/5
 */
public interface ISerializer {

    /**
     * 序列化方法
     * @param obj
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化方法
     * @param data
     * @param classOfT
     * @return
     */
    <T> T deserialize(final byte[] data, Class<T> classOfT);
}
