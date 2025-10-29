package com.max.store.service;

import com.max.store.client.PaymentServiceClient;
import com.max.store.client.ReserveServiceClient;
import com.max.store.dto.*;
import com.max.store.event.ProductInfoRequestEvent;
import com.max.store.event.ProductInfoResponseEvent;
import com.max.store.kafka.ProductInfoProducer;
import com.max.store.kafka.ProductInfoResponseStorage;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Service
public class StoreServiceImpl implements StoreService {

    private final ReserveServiceClient reserveServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final ProductInfoProducer productInfoProducer;
    private final ProductInfoResponseStorage productInfoResponseStorage;

    public ProductResponse getProductById(Long productId) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("Начало процесса получения информации о продукте: productId={}", productId);

        try {
            // Создаем асинхронное ожидание
            CompletableFuture<ProductInfoResponseEvent> future =
                    productInfoResponseStorage.createPendingRequest(correlationId);

            // Отправляем запрос
            ProductInfoRequestEvent requestEvent = new ProductInfoRequestEvent(productId, correlationId);
            productInfoProducer.sendRequest(requestEvent);

            // Ждем результат асинхронно
            ProductInfoResponseEvent responseEvent = future.get(5, TimeUnit.SECONDS);

            log.info("Ответ получен: {}", responseEvent.getProductInfo());
            return new ProductResponse(responseEvent.getMessage(), responseEvent.getProductInfo());

        } catch (TimeoutException e) {
            log.error("Таймаут при ожидании ответа от ProductInfo");
            return new ProductResponse("Ответ от ProductInfo не получен (timeout)", null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Ожидание прервано", e);
            return new ProductResponse("Ошибка при ожидании ответа", null);
        } catch (ExecutionException e) {
            log.error("Ошибка при получении ответа", e);
            return new ProductResponse("Ошибка при получении ответа", null);
        } finally {
            MDC.clear();
        }
    }

    @Override
    public PurchaseResponse processPurchase(PurchaseRequest request) {

        MDC.put("correlationId", UUID.randomUUID().toString());
        log.info("Начало процесса покупки: accountId={}, productId={}, quantity={}",
                request.getAccountId(), request.getProductId(), request.getQuantity());

        try {
            ReserveRequest reserveRequest = new ReserveRequest(request.getProductId(), request.getQuantity());


            ReserveResponse reserveResponse;
            try {
                reserveResponse = reserveServiceClient.reserveProduct(reserveRequest);

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
                paymentResponse = paymentServiceClient.processPayment(paymentRequest);
                if (paymentResponse == null || !paymentResponse.isSuccess()) {
                    reserveServiceClient.cancelReserve(reserveRequest);

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
