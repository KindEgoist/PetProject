package com.max.store.fallback;

import com.max.store.client.PaymentServiceClient;
import com.max.store.dto.ActionResponse;
import com.max.store.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentServiceFallback implements PaymentServiceClient {
    @Override
    public ActionResponse processPayment(PaymentRequest request) {
        log.warn("Fallback: Сервис оплаты недоступен, request: {}", request);
        return new ActionResponse(false, "Оплата не доступна (fallback)");
    }
}
