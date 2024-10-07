package com.li.example.provider;

import com.li.example.common.service.UserService;
import com.li.lirpc.bootstrap.ProviderBootstrap;
import com.li.lirpc.model.ServiceRegisterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {

    public static void main(String[] args) {

        //要注册的服务
        List<ServiceRegisterInfo<?>> serviceRegisterInfoList=new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo
                (UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        //服务提供者初始化
        ProviderBootstrap.init(serviceRegisterInfoList);
    }
}
