package com.max.store.client;

import com.max.store.dto.ActionResponse;
import com.max.store.dto.PaymentRequest;
import com.max.store.fallback.PaymentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "payment-service",
        path = "/payment",
        fallback = PaymentServiceFallback.class
)

public interface PaymentServiceClient {

    @PostMapping("/pay")
    ActionResponse processPayment(@RequestBody PaymentRequest request);
}

