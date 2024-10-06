package com.li.lirpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.li.lirpc.config.RegistryConfig;
import com.li.lirpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry{


    /**
     * 本机注册的节点集合（用于维护续期）
     */
    private final Set<String> localRegisterNodeKeySet=new HashSet<>();


    /**
     * 注册中心服务缓存(只支持单个服务缓存)
     */
    @Deprecated
    private final RegistryServiceCache registryServiceCache=new RegistryServiceCache();

    /**
     * 注册中心服务缓存（支持多个服务）
     */
    private final RegistryServiceMultiCache registryServiceMultiCache=new RegistryServiceMultiCache();


    private final Set<String> watchingKeySet=new ConcurrentHashSet<>();


    private Client client;

    private KV kvClient;


    /**
     * 根节点
     */
    public static final String ETCD_ROOT_PATH = "/rpc/";


    @Override
    public void init(RegistryConfig registryConfig) {
        client=Client.builder().endpoints(registryConfig.getAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeOut())).build();
        kvClient=client.getKVClient();

        heartbeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {

        //创建lease和KV客户端
        Lease leaseClient = client.getLeaseClient();

        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //设置要存储的键值对
        String registryKey=ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key,value,putOption).get();

        //添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registryKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {

        String registryKey=ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        try {
            kvClient.delete(ByteSequence.from(registryKey, StandardCharsets.UTF_8)).get();
        } catch (Exception e) {
            throw new RuntimeException(registryKey+"节点删除失败",e);
        }

        //从本地缓存中移除
        localRegisterNodeKeySet.remove(registryKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        //优先从缓存获取服务
        //List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.readCache();

        List<ServiceMetaInfo> cacheServiceMetaInfoList=registryServiceMultiCache.readCache(serviceKey);
        if(cacheServiceMetaInfoList!=null){
            log.info("从缓存加载服务列表");
            return cacheServiceMetaInfoList;
        }

        String searchPrefix=ETCD_ROOT_PATH+serviceKey;
        log.info("搜索前缀："+searchPrefix);

        try {
            //前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), getOption)
                    .get()
                    .getKvs();

            //解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {

                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        //监听key的变化
                        watch(key);

                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());

            //写入服务缓存
            //registryServiceCache.writeCache(serviceMetaInfoList);
            registryServiceMultiCache.writeCache(serviceKey,serviceMetaInfoList);
            log.info("从注册中心加载服务列表");
            return serviceMetaInfoList;
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败：",e);
        }
    }



    @Override
    public void destroy() {

        System.out.println("当前节点下线");
        //下线节点
        //遍历本节点所有的key
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key,StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key+"节点下线失败",e);
            }
        }


        //释放资源
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartbeat() {
        //10秒续签一次
        CronUtil.schedule("*/10 * * * * *", new Task() {

            @Override
            public void execute() {
                //遍历本节点所有的key
                for (String key : localRegisterNodeKeySet) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        //该节点已经过期（需要重启节点才能重新注册）
                        if(CollUtil.isEmpty(keyValues)){
                            continue;
                        }

                        //节点未过期，重新注册（续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);

                    } catch (Exception e) {
                        throw new RuntimeException(key+"续签失败",e);
                    }
                }
            }
        });
        //支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        //之前未监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if(newWatch){
            watchClient.watch(ByteSequence.from(serviceNodeKey,StandardCharsets.UTF_8),response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()){
                        //删除时触发
                        case DELETE:
                            //清除注册服务缓存
                            //registryServiceCache.clearCache();
                            registryServiceMultiCache.clearCache(serviceNodeKey);
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }
}
