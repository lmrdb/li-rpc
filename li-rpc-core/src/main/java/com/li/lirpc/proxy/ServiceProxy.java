package com.li.lirpc.proxy;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.li.lirpc.RpcApplication;
import com.li.lirpc.config.RpcConfig;
import com.li.lirpc.constant.RpcConstant;
import com.li.lirpc.fault.retry.RetryStrategy;
import com.li.lirpc.fault.retry.RetryStrategyFactory;
import com.li.lirpc.loadbalancer.LoadBalancer;
import com.li.lirpc.loadbalancer.LoadBalancerFactory;
import com.li.lirpc.model.RpcRequest;
import com.li.lirpc.model.RpcResponse;
import com.li.lirpc.model.ServiceMetaInfo;
import com.li.lirpc.protocol.*;
import com.li.lirpc.registry.Registry;
import com.li.lirpc.registry.RegistryFactory;
import com.li.lirpc.serializer.JdkSerializer;
import com.li.lirpc.serializer.Serializer;
import com.li.lirpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 服务代理（JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {


    /**
     *调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //指定序列化器
//        Serializer serializer=null;
//        ServiceLoader<Serializer> serviceLoader = ServiceLoader.load(Serializer.class);
//        for (Serializer service : serviceLoader) {
//            serializer=service;
//        }
        // 指定序列化器
 //       final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
        final Serializer serializer = new JdkSerializer();


        //构造请求
        String serviceName = method.getDeclaringClass().getName();
        RpcRequest rpcRequest = RpcRequest.builder()
                .methodName(method.getName())
                .serviceName(serviceName)
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();

        try {
            //序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);

            //发送请求
            // 这里地址被硬编码（需要使用注册中心和服务发现机制解决）
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceNodeKey());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }

            //负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            //将调用方法名作为负载均衡参数
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("serviceName", rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);

            //ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfoList.get(0);
            System.out.println("负载均衡选择："+selectedServiceMetaInfo);


            //发送RPC请求
            //使用重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo));

            //RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);
            return rpcResponse.getData();

        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }
}
