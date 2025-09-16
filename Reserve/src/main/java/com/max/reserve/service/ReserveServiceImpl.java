package com.max.reserve.service;

import com.max.reserve.exception.ProductNotFoundException;
import com.max.reserve.model.Product;
import com.max.reserve.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ReserveServiceImpl implements ReserveService {

    private final ProductRepository productRepository;

    public ReserveServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Резервируем
     * @param productId получаем АЙДИ продукта
     * @param quantity получаем количество
     * @return резерв выполнен или нет
     */
    @Override
    @Transactional
    public boolean reserve(Long productId, int quantity) {

        Product product = getProduct(productId);

        int available = product.getAvailable();
        if (available >= quantity) {
            product.setReserved(product.getReserved() + quantity);
            product.setAvailable(product.getAvailable() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

   /**
     * Совершение покупки
     * @param productId получаем АЙДИ продукта
     * @param quantity получаем количество
     * @return покупка совершина или нет
     */
    @Override
    @Transactional
    public boolean commitReserve(Long productId, int quantity) {

        Product product = getProduct(productId);
        int reserved = product.getReserved();
        if (reserved >= quantity) {
            product.setReserved(product.getReserved() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    /**
     * Отмена покупки
     * @param productId получаем АЙДИ продукта
     * @param quantity получаем количество
     */
    @Override
    @Transactional
    public void cancelReserve(Long productId, int quantity) {

        Product product = getProduct(productId);

        if (product.getReserved() < quantity) {
            throw new RuntimeException("Невозможно отменить резерв: запрошено больше, чем зарезервировано");
        }
        product.setReserved(product.getReserved() - quantity);
        product.setAvailable(product.getAvailable() + quantity);
    }
}
