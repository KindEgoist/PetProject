package com.max.reserve.service;


import com.max.reserve.model.Product;

public interface ReserveService {
    boolean reserve(Long productId, int quantity);
    boolean commitReserve(Long productId, int quantity);
    void cancelReserve(Long productId, int quantity);
    Product getProduct(Long id);
}
