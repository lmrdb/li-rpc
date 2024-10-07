package com.li.lirpcspringbootstarter.annotation;


import com.li.lirpc.constant.RpcConstant;
import com.li.lirpc.fault.retry.RetryStrategyKeys;
import com.li.lirpc.fault.tolerant.TolerantStrategyKeys;
import com.li.lirpc.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 服务消费者注解（已经注册服务）
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {


    /**
     * 服务接口类
     * @return
     */
    Class<?> interfaceClass() default void.class;


    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;


    String serviceLoadBalancer() default LoadBalancerKeys.ROUND_ROBIN;


    String retryStrategy() default RetryStrategyKeys.NO;


    String tolerantStrategy() default TolerantStrategyKeys.FAIL_SAFE;


    boolean mock() default false;
}
