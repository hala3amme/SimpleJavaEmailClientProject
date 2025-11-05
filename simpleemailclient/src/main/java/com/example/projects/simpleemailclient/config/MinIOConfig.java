package com.example.projects.simpleemailclient.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO configuration for object storage
 * Stores MIME content and email attachments
 */
@Configuration
public class MinIOConfig {

    @Value("${minio.endpoint:http://localhost:9000}")
    private String endpoint;

    @Value("${minio.access-key:minioadmin}")
    private String accessKey;

    @Value("${minio.secret-key:minioadmin}")
    private String secretKey;

    @Value("${minio.bucket.messages:email-messages}")
    private String messagesBucket;

    @Value("${minio.bucket.attachments:email-attachments}")
    private String attachmentsBucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }

    public String getMessagesBucket() {
        return messagesBucket;
    }

    public String getAttachmentsBucket() {
        return attachmentsBucket;
    }
}
