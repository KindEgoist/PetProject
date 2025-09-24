package com.max.reserve.controller;

import com.max.reserve.dto.ActionResponse;
import com.max.reserve.dto.ProductActionRequest;
import com.max.reserve.dto.ReserveResponse;
import com.max.reserve.exception.ProductNotFoundException;
import com.max.reserve.model.Product;
import com.max.reserve.service.ReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/reserve")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    @PostMapping("/res")
    public ResponseEntity<ReserveResponse> reserveProduct(@RequestBody ProductActionRequest request) {

        log.info("Запрос на резерв: productId={}, quantity={}", request.getProductId(), request.getQuantity());

        try {
            boolean reserved = reserveService.reserve(request.getProductId(), request.getQuantity());
            if (reserved) {
                Product product = reserveService.getProduct(request.getProductId());

                log.info("Продукт зарезервирован(res): productId={}, quantity={}, price={}",
                        request.getProductId(), request.getQuantity(), product.getPrice());

                return ResponseEntity.ok(new ReserveResponse(true, "Продукт зарезервирован", product.getPrice()));
            } else {

                log.warn("Недостаточно товара для резерва: productId={}, quantity={}",
                        request.getProductId(), request.getQuantity());

                return ResponseEntity.ok(new ReserveResponse(false, "Недостаточно товара для резерва", 0));
            }
        } catch (ProductNotFoundException e) {

            log.error("Продукт не найден: productId={}", request.getProductId());

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReserveResponse(false, e.getMessage(), 0));
        }
    }

    @PostMapping("/commit")
    public ResponseEntity<ActionResponse> commitReserve(@RequestBody ProductActionRequest request) {
        log.info("Запрос на подтверждение резерва (commit): productId={}, quantity={}, correlationId={}",
                request.getProductId(), request.getQuantity());

        try {
            boolean committed = reserveService.commitReserve(request.getProductId(), request.getQuantity());
            String msg = committed ? "Продукт продан" : "Недостаточно товара в резерве";

            if (committed) {

                log.info("Продажа из резерва подтверждена: productId={}, quantity={}",
                        request.getProductId(), request.getQuantity());
            } else {

                log.warn("Недостаточно товара в резерве: productId={}, quantity={}",
                        request.getProductId(), request.getQuantity());
            }
            return ResponseEntity.ok(new ActionResponse(committed, msg));

        } catch (ProductNotFoundException e) {

            log.error("Продукт не найден: productId={}", request.getProductId());

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ActionResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<ActionResponse> cancelReserve(@RequestBody ProductActionRequest request) {

        log.info("Запрос на отмену резерва(cancel): productId={}, quantity={}",
                request.getProductId(), request.getQuantity());

        try {
            reserveService.cancelReserve(request.getProductId(), request.getQuantity());

            log.info("Резерв отменён: productId={}, quantity={}",
                    request.getProductId(), request.getQuantity());

            return ResponseEntity.ok(new ActionResponse(true, "Резерв отменён"));
        } catch (ProductNotFoundException e) {

            log.error("Продукт не найден: productId={}", request.getProductId());

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ActionResponse(false, e.getMessage()));
        } catch (RuntimeException e) {

            log.error("Ошибка при отмене резерва: productId={}, quantity={}, error={}",
                    request.getProductId(), request.getQuantity(), e.getMessage(), e);

            return ResponseEntity.ok(new ActionResponse(false, "Ошибка при отмене: " + e.getMessage()));
        }
    }
}
