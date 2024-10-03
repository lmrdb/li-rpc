package com.li.lirpc.server;

import com.li.lirpc.model.RpcRequest;
import com.li.lirpc.model.RpcResponse;
import com.li.lirpc.registry.LocalRegistry;
import com.li.lirpc.serializer.JdkSerializer;
import com.li.lirpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP请求处理
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {


    @Override
    public void handle(HttpServerRequest request) {

        //指定序列化器
        Serializer serializer=new JdkSerializer();

        //记录日志
        System.out.println("Received request:"+request.method()+" "+request.uri());

        //异步处理HTTP请求
        request.bodyHandler(body->{
            byte[] bytes=body.getBytes();
            RpcRequest rpcRequest=null;
            //反序列化得到RPC请求对象
            try {
                rpcRequest=serializer.deserialize(bytes,RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //构造响应结果对象
            RpcResponse rpcResponse=new RpcResponse();
            //如果请求为null,直接返回
            if(rpcRequest==null){
                rpcResponse.setMessage("rpcRequest is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }


            try {
                //获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(),
                        rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());

                //封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");

            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            //响应
            doResponse(request,rpcResponse,serializer);
        });

    }

    void doResponse(HttpServerRequest request,RpcResponse response,Serializer serializer){

        HttpServerResponse httpServerResponse=request.response()
                .putHeader("content-type","application/json");

        try {
            byte[] serialized = serializer.serialize(response);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
