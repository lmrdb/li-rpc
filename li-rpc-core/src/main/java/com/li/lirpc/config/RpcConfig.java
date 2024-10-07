package com.li.lirpc.config;


import com.li.lirpc.fault.retry.RetryStrategyKeys;
import com.li.lirpc.fault.tolerant.TolerantStrategyKeys;
import com.li.lirpc.loadbalancer.LoadBalancerKeys;
import com.li.lirpc.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC框架配置
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private String name="li-rpc";


    /**
     * 版本
     */
    private String version="1.0";


    /**
     * 服务器主机名
     */
    private String serverHost="localhost";


    /**
     * 服务器端口
     */
    private Integer serverPort=8080;


    /**
     * 模拟调用
     */
    private boolean mock=false;


    /**
     * 默认序列化器
     */
    private String serializer= SerializerKeys.JDK;


    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig=new RegistryConfig();


    /**
     * 负载均衡配置
     */
    private String loadBalancer= LoadBalancerKeys.ROUND_ROBIN;


    /**
     * 重试策略配置
     */
    private String retryStrategy= RetryStrategyKeys.NO;

    /**
     * 容错策略配置
     */
    private String tolerantStrategy= TolerantStrategyKeys.FAIL_SAFE;

}
