package com.max.store.service;

import com.max.store.dto.*;
import com.max.store.event.productInfo.ProductInfoRequestEvent;
import com.max.store.event.productInfo.ProductInfoResponseEvent;
import com.max.store.event.reserve.ReserveRequestEvent;
import com.max.store.event.reserve.ReserveResponseEvent;
import com.max.store.kafka.productInfo.ProductInfoProducer;
import com.max.store.kafka.productInfo.ProductInfoResponseStorage;
import com.max.store.kafka.reserve.ReserveProducer;
import com.max.store.kafka.reserve.ReserveResponseStorage;
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

    private final ProductInfoProducer productInfoProducer;
    private final ProductInfoResponseStorage productInfoResponseStorage;
    private final ReserveProducer reserveProducer;
    private final ReserveResponseStorage reserveResponseStorage;

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

        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        log.info("Начало процесса покупки: accountId={}, productId={}, quantity={}",
                request.getAccountId(), request.getProductId(), request.getQuantity());

        try { //общий

            int totalAmount = 0;

            try { //для резерва
                CompletableFuture<ReserveResponseEvent> future = reserveResponseStorage
                        .createPendingRequest(correlationId);
                ReserveRequestEvent requestEvent = new ReserveRequestEvent(request.getProductId(),
                        request.getQuantity(), correlationId);
                reserveProducer.sendRequest(requestEvent);

                ReserveResponseEvent responseEvent = future.get(5, TimeUnit.SECONDS);

                if (!responseEvent.isSuccess()) {
                    log.warn("Резерв не удался: {}", responseEvent.getMessage());
                    return new PurchaseResponse(false, responseEvent.getMessage());
                }
                log.info("Ответ получен: {}", responseEvent.getMessage(), responseEvent.isSuccess(),
                        responseEvent.getPrice());
                totalAmount = responseEvent.getPrice();

            } catch (TimeoutException e) {
                log.error("Таймаут при ожидании ответа от ProductInfo");
                return new PurchaseResponse(false,"Ответ от Reserve не получен (timeout)");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Ожидание прервано", e);
                return new PurchaseResponse(false,"Ошибка при ожидании ответа");
            } catch (ExecutionException e) {
                log.error("Ошибка при получении ответа", e);
                return new PurchaseResponse(false,"Ошибка при получении ответа");
            }
            try { //для оплаты. Временно
                totalAmount *= request.getQuantity();

                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setAccountId(request.getAccountId());
                paymentRequest.setAmount(totalAmount);
                paymentRequest.setProductId(request.getProductId());
                paymentRequest.setQuantity(request.getQuantity());

                ActionResponse paymentResponse;

                log.info("Покупка успешно завершена");
                return new PurchaseResponse(true, "Покупка успешно завершена!");
            }
            catch (Exception e) {
                return new PurchaseResponse(false,"<UNK> <UNK> <UNK>");
            }


        }
        finally {
            MDC.clear();
        }


    }
}
