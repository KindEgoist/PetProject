package com.max.store.kafka.reserve;

import com.max.store.event.productInfo.ProductInfoResponseEvent;
import com.max.store.event.reserve.ReserveResponseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveResponseConsumerImpl implements ReserveResponseConsumer {

    private final ReserveResponseStorage responseStorage;

    @KafkaListener(topics = "reserve-responses", groupId = "store-reserve-group")
    public void consume(ReserveResponseEvent event) {
        log.info("Получен ответ от Reserve: correlationId={}", event.getCorrelationId());
        responseStorage.completeRequest(event.getCorrelationId(), event);
    }
}
