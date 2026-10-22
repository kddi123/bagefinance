package com.bage.common.config;

import com.aliyun.credentials.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 阿里云短信（已切换为短信宝，注释掉以停用，需要时再放开，不要删除）
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ali.sms", name = "enable", havingValue = "true")
public class ALiSmsConfig {

    final ALiProperties aLiProperties;

    /**
     * 阿里云号码认证服务（Dypnsapi）Client
     * <p>
     * 签名模板在号码认证服务后台配置（固定签名+模板编号）。按官方示例用
     * credentials Client 初始化，并显式传入 yml 中配置的 AccessKey（ali.accessKeyId /
     * ali.accessKeySecret），避免默认凭证链在本地开发机取不到凭证
     * （报错：Unable to load credentials from any of the providers in the chain）。
     * 凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html
     */
    @Bean
    public com.aliyun.dypnsapi20170525.Client dypnsClient() throws Exception {
        // credentials Client 无 setAccessKey* 方法，通过 models.Config 显式注入 AccessKey
        com.aliyun.credentials.models.Config credentialConfig = new com.aliyun.credentials.models.Config()
                .setAccessKeyId(aLiProperties.getAccessKeyId())
                .setAccessKeySecret(aLiProperties.getAccessKeySecret())
                .setType("access_key");
        Client credential = new Client(credentialConfig);
        Config config = new Config()
                .setCredential(credential);
        // Endpoint 请参考 https://api.aliyun.com/product/Dypnsapi
        config.endpoint = aLiProperties.getSms().getEndpoint();
        return new com.aliyun.dypnsapi20170525.Client(config);
    }
}
