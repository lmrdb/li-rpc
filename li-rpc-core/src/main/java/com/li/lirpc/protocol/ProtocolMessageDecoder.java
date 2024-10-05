package com.li.lirpc.protocol;


import com.li.lirpc.model.RpcRequest;
import com.li.lirpc.model.RpcResponse;
import com.li.lirpc.serializer.Serializer;
import com.li.lirpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 协议解码器
 */
public class ProtocolMessageDecoder {


    /**
     * 解码
     * @param buffer
     * @return
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {

        //分别从指定位置读出buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        byte magic = buffer.getByte(0);
        //校验魔数
        if(magic!=ProtocolConstant.PROTOCOL_MAGIC){
            throw new RuntimeException("魔数不匹配");
        }

        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        //解决粘包，只读取指定长度的数据
        byte[] bytes = buffer.getBytes(17, 17 + header.getBodyLength());
        //解析消息体
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum
                .getEnumByKey(header.getSerializer());
        if(serializerEnum==null){
            throw new RuntimeException("序列化协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if(messageTypeEnum==null){
            throw new RuntimeException("序列化消息的类型不存在");
        }

        switch (messageTypeEnum) {
            case REQUEST:
                RpcRequest request= serializer.deserialize(bytes, RpcRequest.class);
                return new ProtocolMessage<>(header, request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bytes, RpcResponse.class);
                return new ProtocolMessage<>(header, response);
            case BEAT:
            case OTHER:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }


    }
}
