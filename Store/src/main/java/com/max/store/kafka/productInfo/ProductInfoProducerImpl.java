package com.max.store.kafka.productInfo;



import com.max.store.event.productInfo.ProductInfoRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoProducerImpl implements ProductInfoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendRequest(ProductInfoRequestEvent event) {
        log.info("Отправка в Kafka: {}", event);

        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send("product-info-requests", event).toCompletableFuture();

            SendResult<String, Object> result = future.get(5, TimeUnit.SECONDS);

            log.info("Сообщение отправлено: topic={}, partition={}, offset={}",
                    result.getRecordMetadata().topic(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

        } catch (TimeoutException e) {
            log.error("Таймаут отправки в Kafka: {}", e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Отправка прервана: {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Ошибка отправки в Kafka: {}", e.getMessage());
        }
    }
}


