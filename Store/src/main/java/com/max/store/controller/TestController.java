package com.max.store.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/test/kafka")
    public String testKafka() {
        if (kafkaTemplate == null) {
            return "KafkaTemplate IS NULL";
        }
        try {
            kafkaTemplate.send("test-topic", "Тестовое сообщение");
            return "KafkaTemplate РАБОТАЕТ!";
        } catch (Exception e) {
            return "KafkaTemplate ERROR!!!: " + e.getMessage();
        }
    }
}