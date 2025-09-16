package com.max.payment.fallback;

import com.max.payment.client.ReserveServiceClient;
import com.max.payment.dto.ActionResponse;
import com.max.payment.dto.PaymentRequest;
import com.max.payment.dto.ReserveRequest;
import org.springframework.stereotype.Component;


@Component
public class ReserveServiceFallback implements ReserveServiceClient {

    @Override
    public ActionResponse commitReserve(ReserveRequest request) {
        return new ActionResponse(false, "Резерв-сервис временно недоступен (fallback)");
    }

    @Override
    public void cancelReserve(PaymentRequest request) {
        System.out.println("Отмена резерва не выполнена (fallback)");
    }

}
