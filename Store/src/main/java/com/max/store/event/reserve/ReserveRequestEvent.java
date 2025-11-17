package com.max.store.event.reserve;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReserveRequestEvent {
    private Long productId;
    private int quantity;
    private String correlationId;
}
