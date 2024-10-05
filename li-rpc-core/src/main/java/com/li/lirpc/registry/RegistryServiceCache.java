package com.li.lirpc.registry;

import com.li.lirpc.model.ServiceMetaInfo;

import java.util.List;

public class RegistryServiceCache {


    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;


    /**
     * 写缓存
     * @param newServiceCache
     */
    void writeCache(List<ServiceMetaInfo> newServiceCache) {
        this.serviceCache = newServiceCache;
    }


    /**
     * 读缓存
     * @return
     */
    List<ServiceMetaInfo> readCache() {
        return this.serviceCache;
    }


    /**
     * 清空缓存
     */
    void clearCache() {
        this.serviceCache.clear();
    }
}
