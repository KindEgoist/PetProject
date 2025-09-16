package com.max.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReserveRequest {
    private Long productId;
    private int quantity;
}
