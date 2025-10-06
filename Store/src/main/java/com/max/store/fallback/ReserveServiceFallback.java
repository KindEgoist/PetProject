package com.max.store.fallback;

import com.max.store.client.ReserveServiceClient;
import com.max.store.dto.ActionResponse;
import com.max.store.dto.ReserveRequest;
import com.max.store.dto.ReserveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class ReserveServiceFallback implements ReserveServiceClient {

    @Override
    public ReserveResponse reserveProduct(ReserveRequest request) {
        log.warn("Fallback: ReserveService недоступен для productId={}, quantity={}", request.getProductId(),
                request.getQuantity());
        return new ReserveResponse(false, "Резерв-сервис временно недоступен (fallback)", 0);
    }

    @Override
    public void cancelReserve(ReserveRequest request) {
        log.warn("Fallback: Отмена резерва не выполнена для productId={}, quantity={}", request.getProductId());
                request.getQuantity();
        System.out.println("Отмена резерва не выполнена (fallback)");
    }

    @Override
    public ActionResponse commitReserve(ReserveRequest request) {
        log.warn("Fallback: Подтверждение резерва не выполнено для productId={}, quantity={}", request.getProductId(),
                request.getQuantity());
        return new ActionResponse(false, "Резерв-сервис временно недоступен (fallback)");
    }
}
