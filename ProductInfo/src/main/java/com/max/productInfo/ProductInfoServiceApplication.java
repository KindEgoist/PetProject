package com.max.productInfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableRetry
@EnableCaching
public class ProductInfoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductInfoServiceApplication.class, args);
    }
}
