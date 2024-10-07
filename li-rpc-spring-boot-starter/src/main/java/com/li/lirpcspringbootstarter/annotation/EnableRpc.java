package com.li.lirpcspringbootstarter.annotation;


import com.li.lirpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.li.lirpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.li.lirpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {


    /**
     * 需要启动server
     * @return
     */
    boolean needServer() default true;
}
