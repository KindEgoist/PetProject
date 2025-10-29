package com.max.productInfo.event;

import com.max.productInfo.dto.ProductInfo;
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