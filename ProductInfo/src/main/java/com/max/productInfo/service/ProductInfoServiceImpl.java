package com.max.productInfo.service;

import com.max.productInfo.dto.ProductInfo;
import com.max.productInfo.model.ProductRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductInfoServiceImpl implements ProductInfoService {

    private final ProductRepositoryService productRepositoryService;

    @Override

    public ProductInfo getProductById(Long productId) {

        MDC.put("traceId", UUID.randomUUID().toString());
        log.info("Запрос информации о продукте: productId={}", productId);

        ProductInfo productInfo = productRepositoryService.getProductById(productId);

        log.info("Информация о продукте получена: productId={}", productId);
        MDC.clear();
        return productInfo;
    }
}
