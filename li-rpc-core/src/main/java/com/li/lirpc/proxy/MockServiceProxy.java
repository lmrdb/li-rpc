package com.li.lirpc.proxy;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Mock服务代理（JDK动态代理）
 */
@Slf4j
public class MockServiceProxy implements InvocationHandler {




    /**
     *调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}",method.getName());
        return getDefaultObject(methodReturnType);
    }


    private Object getDefaultObject(Class<?> type){
        //基本类型
        if(type.equals(int.class)){
            return 0;
        }
        if(type.equals(long.class)){
            return 0L;
        }
        if(type.equals(double.class)){
            return 0.0;
        }
        if(type.equals(boolean.class)){
            return false;
        }
        if(type.equals(short.class)){
            return (short)0;
        }
        if(type.equals(byte.class)){
            return (byte)0;
        }
        if(type.equals(char.class)){
            return (char)0;
        }
        //对象类型
        return null;
    }
}
