package com.max.payment.controller;

import com.max.payment.dto.ActionResponse;
import com.max.payment.dto.PaymentRequest;
import com.max.payment.exception.AccountNotFoundException;
import com.max.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<ActionResponse> processPayment(@RequestBody PaymentRequest request) {
        try {
            ActionResponse response = paymentService.processPayment(request);
            return ResponseEntity.ok(response);
        }
        catch (AccountNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ActionResponse(false, e.getMessage()));
        }

    }
}
