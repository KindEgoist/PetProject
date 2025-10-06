package com.max.reserve.model;

import com.max.reserve.exception.ProductNotFoundException;
import com.max.reserve.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public Product getProduct(Long id) {

        log.info("Получение продукта из БД (для изменения): productId={}", id);

        return productRepository.findById(id)
                .orElseThrow(() -> {

                    log.error("Продукт не найден: productId={}", id);

                    return new ProductNotFoundException(id);
                });
    }

    public Product saveProduct(Product product) {

        log.info("Сохранения продукта(обновление) в БД: productId={}", product.getId());

        return productRepository.save(product);
    }

}
