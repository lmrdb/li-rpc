package com.li.lirpc.loadbalancer;

import com.li.lirpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {


    /**
     * 一致性hash环，存放虚拟节点
     */
    private TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();


    private static final int VIRTUAL_NODE_NUM = 100;


    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {


        if(serviceMetaInfoList.isEmpty()){
            return null;
        }

        //构建虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        //获取调用请求的hash值
        int hash = getHash(requestParams);

        //选择最近且大于等于调用请求hash值的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);

        if(entry == null){
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }



    private int getHash(Object key) {
        return key.hashCode();
    }
}
