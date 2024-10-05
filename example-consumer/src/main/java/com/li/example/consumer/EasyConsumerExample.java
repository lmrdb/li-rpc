package com.li.example.consumer;

import com.li.example.common.model.User;
import com.li.example.common.service.UserService;
import com.li.lirpc.config.RpcConfig;
import com.li.lirpc.utils.ConfigUtils;
import com.li.lirpc.proxy.ServiceProxyFactory;
/**
 * 简易服务消费者
 */
public class EasyConsumerExample {
    public static void main(String[] args) throws InterruptedException {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        // 需要获取UserService的实体类对象
        UserService userService=ServiceProxyFactory.getProxy(UserService.class);
        User user=new User();
        user.setName("韩天尊");
        User newUser=userService.getUser(user);
        if(newUser!=null){
            System.out.println(newUser.getName());
        }else {
            System.out.println("user is null");
        }
//        newUser=userService.getUser(user);
//        if(newUser!=null){
//            System.out.println(newUser.getName());
//        }else {
//            System.out.println("user is null");
//        }
//        Thread.sleep(10000);
//        newUser=userService.getUser(user);
//        if(newUser!=null){
//            System.out.println(newUser.getName());
//        }else {
//            System.out.println("user is null");
//        }
        long number=userService.getNumber();
        System.out.println(number);
    }
}
