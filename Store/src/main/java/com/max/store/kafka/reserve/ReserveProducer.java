package com.max.store.kafka.reserve;


import com.max.store.event.reserve.ReserveRequestEvent;

public interface ReserveProducer {
    void sendRequest(ReserveRequestEvent event);
}
