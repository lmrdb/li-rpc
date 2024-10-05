package com.li.lirpc.registry;

import com.li.lirpc.config.RegistryConfig;
import com.li.lirpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Registry {


    /**
     * 初始化
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);


    /**
     * 注册服务
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;


    /**
     * 注销服务
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);


    /**
     * 服务发现
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);


    /**
     * 服务销毁
     */
    void destroy();


    /**
     * 心跳检测
     */
    void heartbeat();


    /**
     * 监听（消费端）
     * @param serviceNodeKey
     */
    void watch(String serviceNodeKey);
}
