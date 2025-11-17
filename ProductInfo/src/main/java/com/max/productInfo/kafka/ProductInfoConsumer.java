package com.max.productInfo.kafka;

import com.max.productInfo.dto.ProductInfo;
import com.max.productInfo.event.ProductInfoRequestEvent;
import com.max.productInfo.event.ProductInfoResponseEvent;
import com.max.productInfo.service.ProductInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoConsumer {

    private final ProductInfoService productInfoService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "product-info-requests", groupId = "store-product-info-group")
    public void consume(ProductInfoRequestEvent event) {
        log.info("Получен запрос из Kafka: productId={}, correlationId={}",
                event.getProductId(), event.getCorrelationId());

        try {
            ProductInfo product = productInfoService.getProductById(event.getProductId());
            ProductInfoResponseEvent response = new ProductInfoResponseEvent(
                    event.getCorrelationId(),
                    product,
                    "Информация успешно получена"
            );
            kafkaTemplate.send("product-info-responses", response);
            log.info("Ответ отправлен в Kafka: correlationId={}", event.getCorrelationId());
        } catch (Exception e) {
            log.error("Ошибка обработки Kafka сообщения: {}", e.getMessage(), e);
            kafkaTemplate.send("product-info-responses",
                    new ProductInfoResponseEvent(event.getCorrelationId(), null,
                            "Ошибка: " + e.getMessage()));
        }
    }
}
