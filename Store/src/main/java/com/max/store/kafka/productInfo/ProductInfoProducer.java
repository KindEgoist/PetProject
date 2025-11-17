package com.max.store.kafka.productInfo;

import com.max.store.event.productInfo.ProductInfoRequestEvent;

public interface ProductInfoProducer {
    void sendRequest(ProductInfoRequestEvent event);
}
