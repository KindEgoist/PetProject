package com.max.store.kafka;

import com.max.store.event.PurchaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseEventProducer {

    private final KafkaTemplate<String, PurchaseEvent> kafkaTemplate;

    public void sendPurchaseEvent(PurchaseEvent event) {
        log.info("📦 Отправка события в Kafka: {}", event);
        kafkaTemplate.send("purchase-requests", event);
    }
}

