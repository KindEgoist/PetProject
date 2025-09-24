package com.max.payment.service;

import com.max.payment.client.ReserveServiceClient;
import com.max.payment.dto.ActionResponse;
import com.max.payment.dto.PaymentRequest;
import com.max.payment.dto.ReserveRequest;
import com.max.payment.exception.AccountNotFoundException;
import com.max.payment.model.Account;
import com.max.payment.repository.AccountRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AccountRepository accountRepository;
    private final ReserveServiceClient reserveServiceClient;

    @Override
    @Transactional
    public ActionResponse processPayment(PaymentRequest request) {

        MDC.put("correlationId", UUID.randomUUID().toString());

        log.info("Начала обработка оплаты: accountId={}, amount={}",
                request.getAccountId(), request.getAmount());
        try {
            Account account = accountRepository.findById(request.getAccountId())
                    .orElseThrow(() -> {

                        log.error("Аккаунт не найден: accountId={}", request.getAccountId());

                        return new AccountNotFoundException(request.getAccountId());
                    });

            if (account.getBalance() < request.getAmount()) {

                log.warn("Недостаточно средств на счете: accountId={}, balance={}, required={}",
                        request.getAccountId(), account.getBalance(), request.getAmount());

                reserveServiceClient.cancelReserve(request);
                return new ActionResponse(false, "Недостаточно средств");
            }

            ReserveRequest reserveRequest = new ReserveRequest(request.getProductId(), request.getQuantity());

            ActionResponse commitResponse;

            try {

                commitResponse = reserveServiceClient.commitReserve(reserveRequest);
                if (commitResponse == null || !commitResponse.isSuccess()) {

                    log.warn("Не удалось подтвердить резерв на складе: productId={}, quantity={}",
                            request.getProductId(), request.getQuantity());

                    return new ActionResponse(false, "Не удалось подтвердить резерв на складе");
                }
            } catch (FeignException e) {
                log.error("Ошибка связи с сервисом резервирования. Status: {}, Message: {}",
                        e.status(), e.contentUTF8());
                return new ActionResponse(false, "Сервис резервирования временно недоступен" );
            }

            account.setBalance(account.getBalance() - request.getAmount());

            log.info("Оплата успешно обработана: accountId={}, amount={}",
                    request.getAccountId(), request.getAmount());

            return new ActionResponse(true, "Оплата прошла успешно");
        }
        catch (Exception e) {

            log.error("Ошибка при обработке платежа: accountId={}, error={}",
                    request.getAccountId(), e.getMessage(), e);

            throw e;
        } finally {
            MDC.clear();
        }

    }
}
