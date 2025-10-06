package com.max.productInfo.model;


import com.max.productInfo.dto.ProductInfo;
import com.max.productInfo.exception.ProductNotFoundException;
import com.max.productInfo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryService {
    private final ProductRepository productRepository;

    @Cacheable(value = "products", key = "#id")
    public ProductInfo getProductById(Long id) {

        log.info("Получения продукта из базы данных (для отображения): productId=id{}", id);

        return productRepository.findProductInfoById(id)
                .orElseThrow(() -> {

                    log.error("Продукт не найден: productId={}", id);

                    return new ProductNotFoundException(id);
                });
    }

}
