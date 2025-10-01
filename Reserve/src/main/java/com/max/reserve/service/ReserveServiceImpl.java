package com.max.reserve.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveServiceImpl implements ReserveService {

    private final ReserveRetryService reserveRetryService;

    @Override
    @Transactional
    public boolean reserve(Long productId, int quantity) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начало резервирования товара: productId={} quantity={} ", productId, quantity);

        try {
            return reserveRetryService.reserveWithRetry(productId, quantity);
        }
        finally {
            MDC.clear();
        }
    }

    @Override
    @Transactional
    public boolean commitReserve(Long productId, int quantity) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начала продажи товара из резерва: productId={} quantity={}", productId, quantity);

        try {
            return reserveRetryService.commitReserveWithRetry(productId, quantity);
        }
        finally {
            MDC.clear();
        }
    }

    @Override
    @Transactional
    public void cancelReserve(Long productId, int quantity) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начало отмены резерва товара: productId={} quantity={}", productId, quantity);

        try {
            reserveRetryService.cancelReserveWithRetry(productId, quantity);
        } finally {
            MDC.clear();
        }
    }
}
