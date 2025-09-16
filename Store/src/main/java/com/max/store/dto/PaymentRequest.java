package com.max.store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long accountId;
    private int amount;
    private Long productId;
    private int quantity;
}
