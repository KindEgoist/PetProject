package com.max.store.kafka;

import com.max.store.event.ProductInfoResponseEvent;

public interface ProductInfoResponseConsumer {

    void consume(ProductInfoResponseEvent event);
}
