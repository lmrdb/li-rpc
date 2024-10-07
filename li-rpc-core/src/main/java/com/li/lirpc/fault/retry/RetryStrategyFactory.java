package com.li.lirpc.fault.retry;


import com.li.lirpc.spi.SpiLoader;

/**
 * 重试策略工厂
 */
public class RetryStrategyFactory {



    static {
        SpiLoader.load(RetryStrategy.class);
    }


    /**
     * 默认重试策略
     */
    private static final RetryStrategy DEFAULT_RETRY_STRATEGY = new NoRetryStrategy();


    /**
     * 获取实例
     * @param key
     * @return
     */
    public static RetryStrategy getInstance(String key) {
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }



}
