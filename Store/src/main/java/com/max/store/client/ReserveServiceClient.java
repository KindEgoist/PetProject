package com.max.store.client;

import com.max.store.dto.ActionResponse;
import com.max.store.dto.ReserveRequest;
import com.max.store.dto.ReserveResponse;
import com.max.store.fallback.ReserveServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(
        name = "reserve-service",
        path = "/reserve",
        fallback = ReserveServiceFallback.class
)

public interface ReserveServiceClient {

    @PostMapping("/res")
    ReserveResponse reserveProduct(@RequestBody ReserveRequest request);

    @PostMapping("/cancel")
    void cancelReserve(@RequestBody ReserveRequest request);

    @PostMapping("/commit")
    ActionResponse commitReserve(@RequestBody ReserveRequest request);
}

