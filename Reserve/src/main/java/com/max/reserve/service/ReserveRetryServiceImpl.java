package com.max.reserve.service;

import com.max.reserve.model.Product;
import com.max.reserve.model.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReserveRetryServiceImpl implements ReserveRetryService {

    private final ProductService productService;

    @Override
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,
                backoff = @Backoff(delay = 200, multiplier = 2))
    public boolean reserveWithRetry(Long productId, int quantity) {
        try {
            Product product = productService.getProduct(productId);

            int available = product.getAvailable();
            if (available >= quantity) {
                product.setReserved(product.getReserved() + quantity);
                product.setAvailable(product.getAvailable() - quantity);
                productService.saveProduct(product);
                productService.evictProductCache(productId);

                log.info("Товар зарезервирован: productId={} quantity={} reserved={} available={}",
                        productId, quantity, product.getReserved(), product.getAvailable());

                return true;
            }

            log.warn("Недостаточно товара для резерва: productId={} quantity={} available={}",
                    productId, quantity, product.getAvailable());

            return false;
            }
            catch (ObjectOptimisticLockingFailureException e) {
                log.warn("Конфликт версий при резервирование! productId={}. Повторная попытка...", productId);
                throw e;
            }
    }


    @Override
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2))
    public boolean commitReserveWithRetry(Long productId, int quantity) {
        try {
            Product product = productService.getProduct(productId);
            int reserved = product.getReserved();
            if (reserved >= quantity) {
                product.setReserved(product.getReserved() - quantity);
                productService.saveProduct(product);

                log.info("Товар продан из резерва: productId={} quantity={} reserved={} available={}",
                        productId, quantity, product.getReserved(), product.getAvailable());

                return true;
            }
            log.warn("Недостаточно товара в резерве для продажи: productId={} quantity={} reserved={}",
                    productId, quantity, product.getReserved());
            return false;
        }catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Конфликт версий при коммите! productId={}. Повторная попытка...", productId);
            throw e;
        }
    }

    @Override
    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2))
    public void cancelReserveWithRetry (Long productId, int quantity) {
        try {
            Product product = productService.getProduct(productId);

            if (product.getReserved() < quantity) {

                log.error("Невозможно отменить резерв: запрошено больше, чем зарезервировано. " +
                                "productId={} requested={} reserved={}",
                        productId, quantity, product.getReserved());

                throw new RuntimeException("Невозможно отменить резерв: запрошено больше, чем зарезервировано");
            }
            product.setReserved(product.getReserved() - quantity);
            product.setAvailable(product.getAvailable() + quantity);
            productService.saveProduct(product);

            log.info("Резерв отменён: productId={} quantity={} reserved={} available={}",
                    productId, quantity, product.getReserved(), product.getAvailable());
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Конфликт версий при отмене резерва! productId={}. Повторная попытка...", productId);
            throw e;
        }
    }
}
