package com.max.store.service;

import com.max.store.client.PaymentServiceClient;
import com.max.store.client.ReserveServiceClient;
import com.max.store.dto.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final ReserveServiceClient reserveServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @Override
    public PurchaseResponse processPurchase(PurchaseRequest request) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начало процесса покупки: accountId={}, productId={}, quantity={}",
                request.getAccountId(), request.getProductId(), request.getQuantity());

        try {
            ReserveRequest reserveRequest = new ReserveRequest(request.getProductId(), request.getQuantity());


            ReserveResponse reserveResponse;
            try {
                reserveResponse = reserveServiceClient.reserve(reserveRequest);

                if (reserveResponse == null) {

                    log.warn("Сервис резервирования недоступен");

                    return new PurchaseResponse(false, "Сервис резервирования недоступен");
                }

                if (!reserveResponse.isSuccess()) {

                    log.warn("Не удалось зарезервировать товар: {}", reserveResponse.getMessage());

                    return new PurchaseResponse(false,
                            "Ошибка резервирования: " + reserveResponse.getMessage());
                }
            }catch (FeignException e) {
                log.error("Ошибка связи с сервисом резервирования. Status: {}, Message: {}",
                        e.status(), e.contentUTF8());
                String userMessage = e.status() == 503 ?
                        "Сервис резервирования временно недоступен" :
                        "Ошибка при резервировании товара";
                return new PurchaseResponse(false, userMessage);
            }


            int totalAmount = reserveResponse.getPrice() * request.getQuantity();

            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setAccountId(request.getAccountId());
            paymentRequest.setAmount(totalAmount);
            paymentRequest.setProductId(request.getProductId());
            paymentRequest.setQuantity(request.getQuantity());

            ActionResponse paymentResponse;

            try {
                paymentResponse = paymentServiceClient.pay(paymentRequest);
                if (paymentResponse == null || !paymentResponse.isSuccess()) {
                    reserveServiceClient.cancel(reserveRequest);

                    log.warn("Оплата не удалась: {}",
                            paymentResponse != null ? paymentResponse.getMessage() : "Нет ответа");

                    return new PurchaseResponse(false, "Оплата не удалась: " +
                            (paymentResponse != null ? paymentResponse.getMessage() : "Нет ответа"));
                }
            }catch (FeignException e) {
                log.error("Ошибка связи с сервисом оплаты. Status: {}, Message: {}",
                        e.status(), e.contentUTF8());

                String userMessage = e.status() == 503 ?
                        "Сервис оплаты временно недоступен" :
                        "Ошибка при обработке оплаты";
                return new PurchaseResponse(false, userMessage);
            }

          log.info("Покупка успешно завершена");
            return new PurchaseResponse(true, "Покупка успешно завершена!");
        }
        finally {
            MDC.clear();
        }

    }
}
