package com.max.reserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReserveResponse {
    private boolean success;
    private String message;
    private int price;
}
