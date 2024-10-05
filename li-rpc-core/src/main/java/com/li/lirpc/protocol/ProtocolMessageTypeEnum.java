package com.li.lirpc.protocol;


import lombok.Getter;


/**
 * 协议消息的状态枚举
 */
@Getter
public enum ProtocolMessageTypeEnum {


    REQUEST(0),
    RESPONSE(1),
    BEAT(2),
    OTHER(3);




    private final int key;


    ProtocolMessageTypeEnum(int key) {
        this.key = key;
    }


    /**
     * 根据key获取枚举
     * @param key
     * @return
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum anEnum : ProtocolMessageTypeEnum.values()) {
            if (anEnum.key == key) {
                return anEnum;
            }
        }
        return null;
    }
}
