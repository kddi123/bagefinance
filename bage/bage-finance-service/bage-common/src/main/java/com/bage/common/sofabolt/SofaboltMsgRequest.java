package com.bage.common.sofabolt;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SofaboltMsgRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5548808634230832604L;

    /**
     * 设备id
     */
    private String clientId;

    /**
     * 客户端所在服务器地址
     */
    private String serverAddress;

    /**
     * 用户通道id
     */
    private String channelId;

    /**
     * 命令类型
     */
    private String cmd;
}
