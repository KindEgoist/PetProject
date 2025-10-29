package com.max.store.kafka;

import com.max.store.event.ProductInfoRequestEvent;

public interface ProductInfoProducer {
    void sendRequest(ProductInfoRequestEvent event);
}
