package com.li.lirpc.serializer;


import com.li.lirpc.spi.SpiLoader;

/**
 * 序列化工厂（用于获取序列化器对象）
 */
public class SerializerFactory {


//    /**
//     * 序列化映射
//     */
//    public static final Map<String,Serializer> KEY_SERIALIZER_MAP = new HashMap<String,Serializer>(){{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//    }};

    static {
        SpiLoader.load(Serializer.class);
    }


    /**
     * 默认序列化器
     */
//    public static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get(SerializerKeys.JDK);
     public static final Serializer DEFAULT_SERIALIZER=new JdkSerializer();


    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {

//        return KEY_SERIALIZER_MAP.getOrDefault(key,DEFAULT_SERIALIZER);
        return SpiLoader.getInstance(Serializer.class, key);
    }
}
