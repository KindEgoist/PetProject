package com.max.reserve.service;

import org.springframework.stereotype.Service;

@Service
public interface ReserveService {
    boolean reserve(Long productId, int quantity);
    boolean commitReserve(Long productId, int quantity);
    void cancelReserve(Long productId, int quantity);

}
