package com.li.lirpcspringbootstarter.bootstrap;


import com.li.lirpc.RpcApplication;
import com.li.lirpc.config.RegistryConfig;
import com.li.lirpc.config.RpcConfig;
import com.li.lirpc.model.ServiceMetaInfo;
import com.li.lirpc.model.ServiceRegisterInfo;
import com.li.lirpc.registry.LocalRegistry;
import com.li.lirpc.registry.Registry;
import com.li.lirpc.registry.RegistryFactory;
import com.li.lirpcspringbootstarter.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

@Slf4j
public class RpcProviderBootstrap implements BeanPostProcessor {


    /**
     * Bean初始化后，执行注册服务
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if (rpcService!=null){
            //需要注册服务
            //1.获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            //默认值处理
            if (interfaceClass==void.class){
                 interfaceClass= beanClass.getInterfaces()[0];
            }
            String serviceName =interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            //2.注册服务
            //本地注册
            LocalRegistry.register(serviceName,beanClass);

            //全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

            //注册服务到注册中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName+"服务注册失败",e);
            }
        }

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);

    }
}
