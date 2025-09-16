package com.max.payment.service;


import com.max.payment.dto.ActionResponse;
import com.max.payment.dto.PaymentRequest;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    ActionResponse processPayment(PaymentRequest request);
}
