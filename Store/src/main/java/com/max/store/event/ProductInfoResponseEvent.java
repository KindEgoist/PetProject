package com.max.store.event;

import com.max.store.dto.ProductInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoResponseEvent {
    private String correlationId;
    private ProductInfo productInfo;
    private String message;
}