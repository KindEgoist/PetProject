package com.max.store.fallback;

import com.max.store.client.ReserveServiceClient;
import com.max.store.dto.ActionResponse;
import com.max.store.dto.ReserveRequest;
import com.max.store.dto.ReserveResponse;
import org.springframework.stereotype.Component;

@Component
public class ReserveServiceFallback implements ReserveServiceClient {

    @Override
    public ReserveResponse reserve(ReserveRequest request) {
        return new ReserveResponse(false, "Резерв-сервис временно недоступен (fallback)", 0);
    }

    @Override
    public void cancel(ReserveRequest request) {
        System.out.println("Отмена резерва не выполнена (fallback)");
    }

    @Override
    public ActionResponse commit(ReserveRequest request) {
        return new ActionResponse(false, "Резерв-сервис временно недоступен (fallback)");
    }
}
