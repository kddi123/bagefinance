package com.bage.common.netty;

import lombok.Data;

@Data
public class WebSocketUser {
    /**
     * 命令编码
     */
    private Integer cmd;

    /**
     * 登录后的token
     */
    private String token;

    /**
     * 客户端设备id
     */
    private String clientId;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 平台
     */
    private String platform;
}
