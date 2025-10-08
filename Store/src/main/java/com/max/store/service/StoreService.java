package com.max.store.service;


import com.max.store.dto.ProductResponse;
import com.max.store.dto.PurchaseRequest;
import com.max.store.dto.PurchaseResponse;
import org.springframework.stereotype.Service;

@Service
public interface StoreService {
    PurchaseResponse processPurchase(PurchaseRequest request);
    ProductResponse getProductById(Long productId);

}
