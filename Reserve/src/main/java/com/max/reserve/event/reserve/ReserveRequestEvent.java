package com.max.reserve.event.reserve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveRequestEvent {
    private Long productId;
    private int quantity;
    private String correlationId;
}
