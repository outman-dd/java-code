package code.serialize;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 〈序列化工厂〉<p>
 * 〈功能详细描述〉
 *
 * @author zixiao
 * @date 18/6/5
 */
public class SerializeFactory {

    private static ConcurrentHashMap<SerializeType,ISerializer> instances = new ConcurrentHashMap<SerializeType,ISerializer>();

    public static <T> byte[] serialize(SerializeType serializeType, T obj){
        return getSerializer(serializeType).serialize(obj);
    }

    public static <T> T deserialize(SerializeType serializeType, byte[] data, Class<T> classOfT){
        return getSerializer(serializeType).deserialize(data, classOfT);
    }

    public static ISerializer getSerializer(SerializeType serializeType){
        ISerializer serializer = instances.get(serializeType);
        if(serializer == null){
            instances.putIfAbsent(serializeType, newSerializer(serializeType));
        }
        return instances.get(serializeType);
    }

    private static ISerializer newSerializer(SerializeType serializeType){
        switch (serializeType){
            case JSON:
                return new JsonSerializer();
            case PROTOSTUFF:
                return new ProtostuffSerializer();
            case Hessian:
                return new HessianSerializer();
            default:
                throw new IllegalArgumentException("非法的序列化类型："+serializeType);
        }
    }

}
