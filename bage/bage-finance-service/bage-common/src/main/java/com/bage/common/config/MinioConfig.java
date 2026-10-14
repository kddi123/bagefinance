package com.bage.common.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@ConfigurationProperties(prefix = "minio")
@ConditionalOnProperty("minio.endpoint")
@Configuration
public class MinioConfig {
    // 资源的 访问 URL
    @Value("${minio.base-url}")
    private String baseUrl;

    // API 端点
    @Value("${minio.endpoint}")
    private String endpoint;

    // Bucket 存储桶
    @Value("${minio.bucket}")
    private String bucket;

    // Acess Key
    @Value("${minio.access-key}")
    private String accessKey;

    // Secret Key
    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
        return minioClient;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBucket() {
        return bucket;
    }

    public String getFileUrl(String filePath) {
        return String.format("{}/{}/{}", baseUrl, bucket, filePath);
    }
}
