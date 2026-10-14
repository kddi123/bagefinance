package com.bage.common.netty;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "netty.server")
@ConditionalOnProperty(prefix = "netty.server", name = "enable", havingValue = "true")
@Data
public class NettyConfig {
    /**
     * 是否启用
     */
    @Value("false")
    private Boolean enable;

    /**
     * 端口号
     */
    @Value("")
    private Integer port;

    /**
     * websocket路径
     */
    @Value("/ws")
    private String websocketPath;

    /**
     * 服务名称（用于注册到nacos服务注册中心）
     */
    @Value("default_server_name")
    private String serverName;

    /**
     * 队列的长度
     */
    @Value("500")
    private Integer queueSize;
}
