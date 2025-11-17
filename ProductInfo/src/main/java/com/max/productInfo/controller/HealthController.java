package com.max.productInfo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final RedisConnectionFactory redisConnectionFactory;
    private final KafkaAdmin kafkaAdmin;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();

        try {
            redisConnectionFactory.getConnection().ping();
            status.put("redis", "UP");
        } catch (Exception e) {
            status.put("redis", "DOWN: " + e.getMessage());
        }

        try (var adminClient = org.apache.kafka.clients.admin.AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            var clusterId = adminClient.describeCluster()
                    .clusterId()
                    .get(Duration.ofSeconds(3).toMillis(), TimeUnit.MILLISECONDS);
            status.put("kafka", "UP (clusterId=" + clusterId + ")");
        } catch (Exception e) {
            status.put("kafka", "DOWN: " + e.getMessage());
        }

        status.put("service", "ProductInfo UP");

        return status;
    }
}
