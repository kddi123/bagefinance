package com.bage.common.sofabolt;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "sofabolt")
@ConditionalOnProperty(prefix = "sofabolt",name = "enable",havingValue = "true")
@Data
public class SofaboltConfig {
    /**
     * 是否启用sofabolt
     */
    private Boolean enable;

    /**
     * 服务器端口
     */
    private Integer serverPort;

    /**
     * 服务器名称
     */
    private String serverName;

    /**
     * 服务器所在组
     */
    private String serverGroup;
}
