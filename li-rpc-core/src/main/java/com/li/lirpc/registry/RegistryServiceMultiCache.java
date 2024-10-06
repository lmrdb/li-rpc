package com.li.lirpc.registry;

import com.li.lirpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegistryServiceMultiCache {


    /**
     * 服务缓存
     */
    Map<String, List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();


    /**
     * 写缓存
     * @param newServiceCache
     */
    void writeCache(String serviceKey, List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache.put(serviceKey, newServiceCache);
    }


    /**
     * 读缓存
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey) {
        return this.serviceCache.get(serviceKey);
    }


    /**
     * 清空缓存
     */
    void clearCache(String serviceKey ) {
        this.serviceCache.remove(serviceKey);
    }
}
