package com.max.productInfo.controller;

import com.max.productInfo.dto.ProductInfo;
import com.max.productInfo.dto.ProductInfoResponse;
import com.max.productInfo.exception.ProductNotFoundException;
import com.max.productInfo.service.ProductInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/productInfo")
@RequiredArgsConstructor
public class ProductInfoController {
    private final ProductInfoService productInfoService;

    @GetMapping("/{productId}")
    ResponseEntity<ProductInfoResponse> getProductInfoById(@PathVariable Long productId) {
        log.info("Запрос информации о продукте: productId={}", productId);
        try {
            ProductInfo productInfo = productInfoService.getProductById(productId);
            ProductInfoResponse productInfoResponse = new ProductInfoResponse("Информация о продукте получена",
                    productInfo);
            return ResponseEntity.ok(productInfoResponse);
        }catch (ProductNotFoundException e){
            log.error("Продукт не найден: productId={}", productId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ProductInfoResponse(e.getMessage(), null));
        }

    }
}
