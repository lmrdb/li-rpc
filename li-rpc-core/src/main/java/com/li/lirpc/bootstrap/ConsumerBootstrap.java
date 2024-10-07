package com.li.lirpc.bootstrap;


import com.li.lirpc.RpcApplication;

/**
 * 服务消费者启动类
 */
public class ConsumerBootstrap {


    /**
     * 初始化
     */
    public static void init(){
        /**
         * RPC框架初始化
         */
        RpcApplication.init();
    }
}
