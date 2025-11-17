package com.max.store.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveResponse {
    private boolean success;
    private String message;
    private int price;
}
