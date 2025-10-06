package com.max.store.client;

import com.max.store.dto.ActionResponse;
import com.max.store.dto.ProductResponse;
import com.max.store.fallback.ProductInfoServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "productInfo-service",
        path = "/productInfo",
        fallback = ProductInfoServiceFallback.class
)

public interface ProductInfoServiceClient {

    @GetMapping
    ProductResponse getProductById(@PathVariable ("productId")Long productId);

}
