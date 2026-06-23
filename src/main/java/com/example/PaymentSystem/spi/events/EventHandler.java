package com.example.PaymentSystem.spi.events;

import com.example.PaymentSystem.domain.event.DomainEvent;

/**
 * Implemented by any bean that wants to react to domain events. The same
 * handlers are invoked regardless of whether events flow through the in-memory
 * bus or are round-tripped through Kafka.
 */
public interface EventHandler {

	boolean supports(String eventType);

	void handle(DomainEvent event);
}
