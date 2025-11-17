package com.max.store.kafka.productInfo;

import com.max.store.event.productInfo.ProductInfoResponseEvent;

public interface ProductInfoResponseConsumer {

    void consume(ProductInfoResponseEvent event);
}
