package com.max.productInfo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoRequestEvent {
    private Long productId;
    private String correlationId;
}
