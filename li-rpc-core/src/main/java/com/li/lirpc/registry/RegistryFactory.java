package com.li.lirpc.registry;

import com.li.lirpc.spi.SpiLoader;

public class RegistryFactory {


    static {
        SpiLoader.load(Registry.class);
    }


    /**
     * 默认注册中心
     */
    public static final Registry DEFAULT_REGISTRY = new EtcdRegistry();


    /**
     * 获取实例
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.getInstance(Registry.class,key);
    }
}
