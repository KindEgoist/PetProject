package com.max.reserve.service;

import org.springframework.stereotype.Service;

@Service
public interface ReserveRetryService {
    boolean reserveWithRetry(Long productId, int quantity);
    boolean commitReserveWithRetry(Long productId, int quantity);
    void cancelReserveWithRetry (Long productId, int quantity);
}
