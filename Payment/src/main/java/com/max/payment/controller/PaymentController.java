package com.max.payment.controller;

import com.max.payment.dto.ActionResponse;
import com.max.payment.dto.PaymentRequest;
import com.max.payment.exception.AccountNotFoundException;
import com.max.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<ActionResponse> processPayment(@RequestBody PaymentRequest request) {

        log.info("Запрос на оплату: accountId={}, amount={}", request.getAccountId(), request.getAmount());

        try {
            ActionResponse response = paymentService.processPayment(request);

            log.info("Ответ на запрос оплаты: success={}, message={}", response.isSuccess(), response.getMessage());

            return ResponseEntity.ok(response);
        }
        catch (AccountNotFoundException e){

            log.error("Аккаунт не найден: accountId={}", request.getAccountId());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ActionResponse(false, e.getMessage()));
        }

    }
}
