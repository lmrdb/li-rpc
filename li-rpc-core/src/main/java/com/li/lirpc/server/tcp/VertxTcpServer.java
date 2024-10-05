package com.li.lirpc.server.tcp;

import com.li.lirpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer {


    private byte[] handleRequest(byte[] requestData){
        //这里编写处理请求的逻辑
        //实例
        return "Hello Client".getBytes();
    }


    @Override
    public void doStart(int port) {
        //创建Vert.x实例
        Vertx vertx = Vertx.vertx();

        //创建TCP服务器
        NetServer server = vertx.createNetServer();

        //处理请求
//        server.connectHandler(socket -> {
//            //处理连接
//            socket.handler(buffer -> {
//                //处理收到的字节数组
//                byte[] requestData = buffer.getBytes();
//
//                //这里自定义字节数组处理逻辑，比如解析请求、调用服务、响应结构等
//                byte[] responseData = handleRequest(requestData);
//
//                //发送响应
//                socket.write(Buffer.buffer(responseData));
//            });
//        });
        server.connectHandler(new TcpServerHandler());


        //启动TCP服务器并指定端口
        server.listen(port,result->{
            if(result.succeeded()){
                System.out.println("TCP Server started on port "+port);
            }else {
                System.out.println("TCP Server failed to start  "+result.cause());
            }
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
