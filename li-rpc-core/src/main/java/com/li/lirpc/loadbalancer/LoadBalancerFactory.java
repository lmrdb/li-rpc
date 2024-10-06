package com.li.lirpc.loadbalancer;


import com.li.lirpc.spi.SpiLoader;

/**
 * 负载均衡器工厂
 */
public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoadBalancer.class);
    }


    /**
     * 默认复载均衡器
     */
    private static final LoadBalancer DEFAULT_LOADBALANCER = new RoundRobinLoadBalancer();


    public static LoadBalancer getInstance(String key) {
        return SpiLoader.getInstance(LoadBalancer.class, key);
    }

}
