package com.example.PaymentSystem.spi.events;

import com.example.PaymentSystem.domain.event.DomainEvent;

/**
 * Port for publishing domain events. Implemented by an in-process bus and by
 * Kafka. The active implementation is chosen via {@code app.components.event-bus}.
 */
public interface EventBus {

	void publish(DomainEvent event);
}
