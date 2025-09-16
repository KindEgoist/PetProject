package com.max.reserve.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductActionRequest {
    private Long productId;
    private int quantity;
}
