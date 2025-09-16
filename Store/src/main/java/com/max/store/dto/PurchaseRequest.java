package com.max.store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PurchaseRequest {
    private Long productId;
    private int quantity;
    private Long accountId;
}
