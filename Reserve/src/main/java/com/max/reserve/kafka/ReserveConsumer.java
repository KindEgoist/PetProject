package com.max.reserve.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.reserve.event.reserve.ReserveRequestEvent;
import com.max.reserve.event.reserve.ReserveResponseEvent;
import com.max.reserve.model.*;
import com.max.reserve.repository.OutboxRepository;
import com.max.reserve.service.product.ProductService;
import com.max.reserve.service.reserve.ReserveService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveConsumer {

    private final ReserveService reserveService;
    private final ProductService productService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "reserve-requests", groupId = "store-reserve-group")
    @Transactional
    public void consume(ReserveRequestEvent event) throws JsonProcessingException {

        String correlationId = event.getCorrelationId();
        log.info("Получен запрос Reserve: {}", correlationId);

        boolean reserve = reserveService.reserve(event.getProductId(), event.getQuantity());
        int price = reserve ? productService.getProduct(event.getProductId()).getPrice() : 0;

        ReserveResponseEvent response =
                new ReserveResponseEvent(correlationId, reserve,
                        reserve ? "Резерв создан" : "Недостаточно товара", price);

        outboxRepository.save(
                OutboxMessage.builder()
                        .id(UUID.randomUUID())
                        .correlationId(correlationId)
                        .topic("reserve-responses")
                        .eventType(ReserveResponseEvent.class.getName())
                        .payload(objectMapper.valueToTree(response))
                        .status(Status.NEW)
                        .createdAt(Instant.now())
                        .build()
        );
    }

}
