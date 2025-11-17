package com.max.store.event.productInfo;

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
