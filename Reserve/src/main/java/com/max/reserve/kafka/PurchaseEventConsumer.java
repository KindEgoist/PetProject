package com.max.reserve.kafka;

import com.max.reserve.event.PurchaseEvent;
import com.max.reserve.event.Status;
import com.max.reserve.service.ReserveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseEventConsumer {

    private final ReserveService reserveService;

    @KafkaListener(topics = "purchase-requests", groupId = "reserve-group")
    public void consume(PurchaseEvent event) {
        log.info("Получено событие из Kafka: {}", event);

        try {
            boolean success = reserveService.reserve(event.getProductId(), event.getQuantity());

            event.setStatus(success ? Status.ВЫПОЛНЕН : Status.НЕВЫПОЛНЕН);
            log.info("Резервирование выполнено: {}", event);

        } catch (Exception e) {
            log.error("Ошибка при обработке события Kafka: {}", e.getMessage(), e);
            event.setStatus(Status.НЕВЫПОЛНЕН);
        }
    }
}
