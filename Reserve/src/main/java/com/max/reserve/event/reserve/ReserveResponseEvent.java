package com.max.reserve.event.reserve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveResponseEvent {
    private String correlationId;
    private boolean success;
    private String message;
    private int price;
}