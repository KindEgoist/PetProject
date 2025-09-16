package com.max.store.service;

import com.max.store.client.PaymentServiceClient;
import com.max.store.client.ReserveServiceClient;
import com.max.store.dto.*;
import org.springframework.stereotype.Service;


@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final ReserveServiceClient reserveServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public PurchaseServiceImpl(ReserveServiceClient reserveServiceClient,
                               PaymentServiceClient paymentServiceClient) {
        this.reserveServiceClient = reserveServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    public PurchaseResponse processPurchase(PurchaseRequest request) {

        ReserveRequest reserveRequest = new ReserveRequest(request.getProductId(), request.getQuantity());
        ReserveResponse reserveResponse = reserveServiceClient.reserve(reserveRequest);

        if (reserveResponse == null) {
            return new PurchaseResponse(false, "Сервис резервирования недоступен");
        }

        if (!reserveResponse.isSuccess()) {
            return new PurchaseResponse(false,
                    "Ошибка резервирования: " + reserveResponse.getMessage());
        }

        int totalAmount = reserveResponse.getPrice() * request.getQuantity();

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAccountId(request.getAccountId());
        paymentRequest.setAmount(totalAmount);
        paymentRequest.setProductId(request.getProductId());
        paymentRequest.setQuantity(request.getQuantity());

        ActionResponse paymentResponse = paymentServiceClient.pay(paymentRequest);

        if (paymentResponse == null || !paymentResponse.isSuccess()) {
            reserveServiceClient.cancel(reserveRequest);
            return new PurchaseResponse(false, "Оплата не удалась: " +
                    (paymentResponse != null ? paymentResponse.getMessage() : "Нет ответа"));
        }

        ActionResponse commitResponse = reserveServiceClient.commit(reserveRequest);
        if (commitResponse == null || !commitResponse.isSuccess()) {
            return new PurchaseResponse(false, "Ошибка при подтверждении резерва: " +
                    (commitResponse != null ? commitResponse.getMessage() : "Нет ответа"));
        }

        return new PurchaseResponse(true, "Покупка успешно завершена!");
    }
}
