package com.max.payment.client;

import com.max.payment.dto.ActionResponse;
import com.max.payment.dto.PaymentRequest;
import com.max.payment.dto.ReserveRequest;
import com.max.payment.fallback.ReserveServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "reserve-service", fallback = ReserveServiceFallback.class)
public interface ReserveServiceClient {

    @PostMapping("/reserve/commit")
    ActionResponse commitReserve(@RequestBody ReserveRequest request);

    @PostMapping("/reserve/cancel")
    void cancelReserve(@RequestBody PaymentRequest request);
}
