package com.intheknowyyc.api.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
@Profile({"test", "integrationTest"})
public class TestStorageConfig {

    @Bean
    @Primary
    public S3Client mockS3Client() {
        return Mockito.mock(S3Client.class);
    }
}