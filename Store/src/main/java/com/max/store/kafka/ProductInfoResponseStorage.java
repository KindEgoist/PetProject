package com.max.store.kafka;

import com.max.store.event.ProductInfoResponseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;


@Service
@Slf4j
@Component
public class ProductInfoResponseStorage {
    private final ConcurrentMap<String, CompletableFuture<ProductInfoResponseEvent>> pendingRequests =
            new ConcurrentHashMap<>();

    public CompletableFuture<ProductInfoResponseEvent> createPendingRequest(String correlationId) {
        CompletableFuture<ProductInfoResponseEvent> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);


        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(new TimeoutException("Таймаут ожидания ответа ProductInfo"));
                pendingRequests.remove(correlationId);
            }
        });

        return future;
    }

    public void completeRequest(String correlationId, ProductInfoResponseEvent response) {
        CompletableFuture<ProductInfoResponseEvent> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }
}
