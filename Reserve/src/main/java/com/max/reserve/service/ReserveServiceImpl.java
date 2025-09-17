package com.max.reserve.service;

import com.max.reserve.exception.ProductNotFoundException;
import com.max.reserve.model.Product;
import com.max.reserve.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class ReserveServiceImpl implements ReserveService {

    private final ProductRepository productRepository;

    public ReserveServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> {

                    log.error("Продукт не найден: productId={}", id);

                    return new ProductNotFoundException(id);
                });
    }


    @Override
    @Transactional
    public boolean reserve(Long productId, int quantity) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начало резервирования товара: productId={} quantity={}", productId, quantity);

        try {
            Product product = getProduct(productId);

            int available = product.getAvailable();
            if (available >= quantity) {
                product.setReserved(product.getReserved() + quantity);
                product.setAvailable(product.getAvailable() - quantity);
                productRepository.save(product);

                log.info("Товар зарезервирован: productId={} quantity={} reserved={} available={}",
                        productId, quantity, product.getReserved(), product.getAvailable());

                return true;
            }

            log.warn("Недостаточно товара для резерва: productId={} quantity={} available={}",
                    productId, quantity, product.getAvailable());

            return false;
        }
        finally {
            MDC.clear();
        }

    }


    @Override
    @Transactional
    public boolean commitReserve(Long productId, int quantity) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начала продажи товара из резерва: productId={} quantity={}", productId, quantity);

        try {
            Product product = getProduct(productId);
            int reserved = product.getReserved();
            if (reserved >= quantity) {
                product.setReserved(product.getReserved() - quantity);
                productRepository.save(product);

                log.info("Товар продан из резерва: productId={} quantity={} reserved={} available={}",
                        productId, quantity, product.getReserved(), product.getAvailable());

                return true;
            }
            log.warn("Недостаточно товара в резерве для продажи: productId={} quantity={} reserved={}",
                    productId, quantity, product.getReserved());
            return false;
        }
        finally {
            MDC.clear();
        }
    }


    @Override
    @Transactional
    public void cancelReserve(Long productId, int quantity) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начало отмены резерва товара: productId={} quantity={}", productId, quantity);

        try {
            Product product = getProduct(productId);

            if (product.getReserved() < quantity) {

                log.error("Невозможно отменить резерв: запрошено больше, чем зарезервировано. " +
                                "productId={} requested={} reserved={}",
                        productId, quantity, product.getReserved());

                throw new RuntimeException("Невозможно отменить резерв: запрошено больше, чем зарезервировано");
            }
            product.setReserved(product.getReserved() - quantity);
            product.setAvailable(product.getAvailable() + quantity);

            log.info("Резерв отменён: productId={} quantity={} reserved={} available={}",
                    productId, quantity, product.getReserved(), product.getAvailable());
        } finally {
            MDC.clear();
        }
    }
}
