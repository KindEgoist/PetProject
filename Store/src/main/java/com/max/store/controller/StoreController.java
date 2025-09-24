package com.max.store.controller;

import com.max.store.dto.PurchaseRequest;
import com.max.store.dto.PurchaseResponse;
import com.max.store.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final PurchaseService purchaseService;

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponse> makePurchase(@RequestBody PurchaseRequest request) {

        log.info("Делаем запрос на покупку: {}", request);

        PurchaseResponse response = purchaseService.processPurchase(request);

        log.info("Покупка осуществлена: {}", response);

        return ResponseEntity.ok(response);
    }
}
