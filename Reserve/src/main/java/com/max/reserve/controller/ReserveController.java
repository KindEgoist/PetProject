package com.max.reserve.controller;

import com.max.reserve.dto.ActionResponse;
import com.max.reserve.dto.ProductActionRequest;
import com.max.reserve.dto.ReserveResponse;
import com.max.reserve.exception.ProductNotFoundException;
import com.max.reserve.model.Product;
import com.max.reserve.service.ReserveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/reserve")
public class ReserveController {

    private final ReserveService reserveService;

    public ReserveController(ReserveService reserveService) {
        this.reserveService = reserveService;
    }

    @PostMapping("/res")
    public ResponseEntity<ReserveResponse> reserveProduct(@RequestBody ProductActionRequest request) {
    try {
        boolean reserved = reserveService.reserve(request.getProductId(), request.getQuantity());
        if (reserved) {
            Product product = reserveService.getProduct(request.getProductId());
            return ResponseEntity.ok(new ReserveResponse(true, "Продукт зарезервирован", product.getPrice()));
        } else {
            return ResponseEntity.ok(new ReserveResponse(false, "Продукт не зарезервирован. " +
                    "Запрашиваемое количества товара больше чем на складе", 0));
        }
    }
    catch (ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ReserveResponse(false, e.getMessage(), 0));
    }

    }

    @PostMapping("/commit")
    public ResponseEntity<ActionResponse> commitReserve(@RequestBody ProductActionRequest request) {
        try {
            boolean committed = reserveService.commitReserve(request.getProductId(), request.getQuantity());
            String msg = committed ? "Продукт продан" : "Недостаточно товара в резерве";
            return ResponseEntity.ok(new ActionResponse(committed, msg));
        }
        catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ActionResponse(false, e.getMessage()));
        }

    }

    @PostMapping("/cancel")
    public ResponseEntity<ActionResponse> cancelReserve(@RequestBody ProductActionRequest request) {
        try {
            try {
                reserveService.cancelReserve(request.getProductId(), request.getQuantity());
                return ResponseEntity.ok(new ActionResponse(true, "Резерв отменён"));
            }
            catch (ProductNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ActionResponse(false, e.getMessage()));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.ok(new ActionResponse(false, "Ошибка при отмене: " + e.getMessage()));
        }
    }


}
