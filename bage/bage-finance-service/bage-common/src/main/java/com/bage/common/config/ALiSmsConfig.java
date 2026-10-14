package com.bage.common.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ali.sms", name = "enable", havingValue = "true")
public class ALiSmsConfig {
    final ALiProperties aLiProperties;

    @Bean
    public AsyncClient asyncClient() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(aLiProperties.getAccessKeyId())
                .accessKeySecret(aLiProperties.getAccessKeySecret())
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region(aLiProperties.getSms().getRegion()) // Region ID
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(aLiProperties.getSms().getEndpoint())
                )
                .build();
        return client;
    }
}
