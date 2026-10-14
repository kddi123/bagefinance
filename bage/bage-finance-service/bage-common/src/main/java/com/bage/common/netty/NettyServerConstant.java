package com.bage.common.netty;

public class NettyServerConstant {
    /**
     * 心跳命令
     */
    public static final Integer CMD_HEARTBEAT = 0;

    public static final String SERVER_ADDR = "SERVER_ADDR";

    public static final String HOST = "HOST";

    public static final String PORT = "PORT";

    public static final String CHANNEL_ID = "CHANNEL_ID";

    /**
     * 客户端id
     */
    public static final String CLIENT_ID = "CLIENT_ID";

    /**
     * 客户端名称
     */
    public static final String CLIENT_NAME = "CLIENT_NAME";

    /**
     * 会员id
     */
    public static final String MEMBER_ID = "MEMBER_ID";

    /**
     * TOKEN
     */
    public static final String TOKEN = "TOKEN";

    public static final String UPDATE_TIME = "UPDATE_TIME";

    /**
     * 定时清理断开连接的用户信息
     */
    public static final long TIMEOUT_THRESHOLD = 10L; // 10秒
}
