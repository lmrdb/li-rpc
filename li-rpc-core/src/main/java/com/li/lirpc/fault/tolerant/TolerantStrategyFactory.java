package com.li.lirpc.fault.tolerant;

import com.li.lirpc.spi.SpiLoader;

public class TolerantStrategyFactory {


    static {
        SpiLoader.load(TolerantStrategy.class);
    }


    /**
     * 默认容错策略
     */
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY=new FailSafeTolerantStrategy();


    /**
     * 获取实例
     * @param key
     * @return
     */
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
