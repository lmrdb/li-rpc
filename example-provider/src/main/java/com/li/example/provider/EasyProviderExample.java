package com.li.example.provider;

import com.li.example.common.service.UserService;
import com.li.lirpc.RpcApplication;
import com.li.lirpc.config.RegistryConfig;
import com.li.lirpc.config.RpcConfig;
import com.li.lirpc.model.ServiceMetaInfo;
import com.li.lirpc.registry.LocalRegistry;
import com.li.lirpc.registry.Registry;
import com.li.lirpc.registry.RegistryFactory;
import com.li.lirpc.server.HttpServer;
import com.li.lirpc.server.VertxHttpServer;
import com.li.lirpc.server.tcp.VertxTcpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {

        RpcApplication.init();

        //注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName,UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 启动 web 服务
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(8080);

        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(8080);
    }
}
