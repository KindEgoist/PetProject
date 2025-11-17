package com.max.reserve.service.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.reserve.model.OutboxMessage;
import com.max.reserve.model.Status;
import com.max.reserve.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class OutboxPublisherImpl implements OutboxPublisher {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publish() {

        List<OutboxMessage> list =
                outboxRepository.findTop20ByStatusOrderByCreatedAtAsc(Status.NEW);

        for (OutboxMessage msg : list) {
            try {
                Class<?> type = Class.forName(msg.getEventType());
                Object payload = objectMapper.treeToValue(msg.getPayload(), type);

                kafkaTemplate.send(msg.getTopic(), payload).get();

                msg.setStatus(Status.SENT);
                outboxRepository.save(msg);

            } catch (Exception e) {
                log.error("Ошибка отправки {}", msg.getId());
            }
        }
    }

}

