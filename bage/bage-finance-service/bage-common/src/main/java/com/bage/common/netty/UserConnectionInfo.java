package com.bage.common.netty;

import lombok.Data;

@Data
public class UserConnectionInfo {
    /**
     * 用户所在服务器ip+端口
     */
    private String serverAddr;

    /**
     * 用户所在服务器ip
     */
    private String host;

    /**
     * 用户所在服务器端口
     */
    private Integer port;

    /**
     * 用户所连接的通道id
     */
    private String channelId;

    /**
     * 用户设备id
     */
    private String clientId;

    /**
     * 用户设备名称
     */
    private String clientName;

    /**
     * 会员id
     */
    private Long memberId;

    /**
     * token
     */
    private String token;
}
