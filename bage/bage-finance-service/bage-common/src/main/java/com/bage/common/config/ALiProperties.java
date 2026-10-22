package com.bage.common.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// 阿里云短信（已切换为短信宝，注释掉以停用，需要时再放开，不要删除）
@Configuration
@ConfigurationProperties(prefix = "ali")
@Data
@ConditionalOnProperty(prefix = "ali.sms", name = "enable", havingValue = "true")
public class ALiProperties {

    private String accessKeyId;

    private String accessKeySecret;

    private ALiSmsConfig sms;

    @Data
    public static class ALiSmsConfig {
        /**
         * 是否启用
         */
        private Boolean enable;
        /**
         * 区域
         */
        private String region;
        /**
         * 端点
         */
        private String endpoint;
    }
}