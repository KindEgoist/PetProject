package com.max.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.max.store.client")
public class StoreAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreAppApplication.class, args);
    }
}
