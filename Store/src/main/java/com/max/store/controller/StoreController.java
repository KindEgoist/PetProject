package com.max.store.controller;

import com.max.store.dto.PurchaseRequest;
import com.max.store.dto.PurchaseResponse;
import com.max.store.service.PurchaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/store")
public class StoreController {

    private final PurchaseService purchaseService;

    public StoreController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> makePurchase(@RequestBody PurchaseRequest request) {
        PurchaseResponse response = purchaseService.processPurchase(request);
        return ResponseEntity.ok(response);
    }
}
