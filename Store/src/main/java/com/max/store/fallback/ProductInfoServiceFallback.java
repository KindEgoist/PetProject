package com.max.store.fallback;

import com.max.store.client.ProductInfoServiceClient;
import com.max.store.dto.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductInfoServiceFallback implements ProductInfoServiceClient {
    @Override
    public ProductResponse getProductById(Long productId) {
        log.warn("Fallback: ProductInfoService недоступен для productId={}", productId);

        return new ProductResponse("Продукт Инфо-сервис временно недоступен (fallback)", null);
    }
}
