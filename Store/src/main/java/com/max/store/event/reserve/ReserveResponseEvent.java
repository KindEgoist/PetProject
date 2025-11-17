package com.max.store.event.reserve;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveResponseEvent {
    private String correlationId;
    private boolean success;
    private String message;
    private int price;
}