package com.intheknowyyc.api.config;

import com.amazonaws.services.s3.AmazonS3;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestStorageConfig {

    @Bean
    @Primary
    public AmazonS3 mockS3Client() {
        return Mockito.mock(AmazonS3.class);
    }
}
