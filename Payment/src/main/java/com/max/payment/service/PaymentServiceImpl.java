package com.max.payment.service;

import com.max.payment.client.ReserveServiceClient;
import com.max.payment.dto.ActionResponse;
import com.max.payment.dto.PaymentRequest;
import com.max.payment.dto.ReserveRequest;
import com.max.payment.exception.AccountNotFoundException;
import com.max.payment.model.Account;
import com.max.payment.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PaymentServiceImpl implements PaymentService {

    private final AccountRepository accountRepository;
    private final ReserveServiceClient reserveServiceClient;

    public PaymentServiceImpl(AccountRepository accountRepository,
                              ReserveServiceClient reserveServiceClient) {
        this.accountRepository = accountRepository;
        this.reserveServiceClient = reserveServiceClient;
    }

    @Override
    @Transactional
    public ActionResponse processPayment(PaymentRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));

        if (account.getBalance() < request.getAmount()) {
            reserveServiceClient.cancelReserve(request);
            return new ActionResponse(false, "Недостаточно средств");
        }

        account.setBalance(account.getBalance() - request.getAmount());

        ReserveRequest reserveRequest = new ReserveRequest(request.getProductId(), request.getQuantity());

        ActionResponse commitResponse = reserveServiceClient.commitReserve(reserveRequest);

        if (commitResponse == null || !commitResponse.isSuccess()) {
            return new ActionResponse(false, "Не удалось подтвердить резерв на складе");
        }

        return new ActionResponse(true, "Оплата прошла успешно");
    }
}
