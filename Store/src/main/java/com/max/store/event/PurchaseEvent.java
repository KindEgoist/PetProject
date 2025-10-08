package com.max.store.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseEvent {
    private Long productId;
    private Long accountId;
    private int quantity;
    private Status status;
}

