package com.max.store.kafka.reserve;

import com.max.store.event.productInfo.ProductInfoResponseEvent;
import com.max.store.event.reserve.ReserveResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.*;


@Service
@Slf4j
@Component
public class ReserveResponseStorage {
    private final ConcurrentMap<String, CompletableFuture<ReserveResponseEvent>> pendingRequests =
            new ConcurrentHashMap<>();
    private final Set<String> processedResponses = ConcurrentHashMap.newKeySet();

    public CompletableFuture<ReserveResponseEvent> createPendingRequest(String correlationId) {
        CompletableFuture<ReserveResponseEvent> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);


        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(new TimeoutException("Таймаут ожидания ответа Reserve"));
                pendingRequests.remove(correlationId);
            }
        });

        return future;
    }

    public void completeRequest(String correlationId, ReserveResponseEvent response) {
        if (processedResponses.contains(correlationId)) {
            log.warn("Получен дубликат ответа: correlationID={}", correlationId);
            return;
        }
        CompletableFuture<ReserveResponseEvent> future = pendingRequests.remove(correlationId);
        if (future != null) {
            processedResponses.add(correlationId);
            future.complete(response);
        }
    }
}
