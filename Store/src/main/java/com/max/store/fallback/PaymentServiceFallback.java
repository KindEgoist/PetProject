package com.max.store.fallback;

import com.max.store.client.PaymentServiceClient;
import com.max.store.dto.ActionResponse;
import com.max.store.dto.PaymentRequest;
import org.springframework.stereotype.Component;


@Component
public class PaymentServiceFallback implements PaymentServiceClient {
    @Override
    public ActionResponse pay(PaymentRequest request) {
        return new ActionResponse(false, "Оплата не доступна (fallback)");
    }
}
