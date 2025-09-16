package com.max.store.service;


import com.max.store.dto.PurchaseRequest;
import com.max.store.dto.PurchaseResponse;
import org.springframework.stereotype.Service;

@Service
public interface PurchaseService {
    PurchaseResponse processPurchase(PurchaseRequest request);
}
