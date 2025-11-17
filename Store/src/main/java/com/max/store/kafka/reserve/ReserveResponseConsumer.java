package com.max.store.kafka.reserve;


import com.max.store.event.reserve.ReserveResponseEvent;

public interface ReserveResponseConsumer {

    void consume(ReserveResponseEvent event);
}
