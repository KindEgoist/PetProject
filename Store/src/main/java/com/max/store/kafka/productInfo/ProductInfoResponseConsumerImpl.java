package com.max.store.kafka.productInfo;

import com.max.store.event.productInfo.ProductInfoResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoResponseConsumerImpl implements ProductInfoResponseConsumer {

    private final ProductInfoResponseStorage responseStorage;

    @KafkaListener(topics = "product-info-responses", groupId = "store-product-info-group")
    public void consume(ProductInfoResponseEvent event) {
        log.info("Получен ответ от ProductInfo: correlationId={}", event.getCorrelationId());
        responseStorage.completeRequest(event.getCorrelationId(), event);
    }
}
