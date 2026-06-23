package com.example.PaymentSystem.spi.events;

import com.example.PaymentSystem.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * In-process substitute for Kafka. Synchronously dispatches events to local
 * handlers. Active when {@code app.components.event-bus=memory} (the default).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.components.event-bus", havingValue = "memory", matchIfMissing = true)
public class InMemoryEventBus implements EventBus {

	private final EventDispatcher dispatcher;

	public InMemoryEventBus(EventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
		log.info("EventBus -> InMemoryEventBus (Kafka substitute)");
	}

	@Override
	public void publish(DomainEvent event) {
		log.debug("Publishing {} (in-memory) for {}", event.type(), event.aggregateId());
		dispatcher.dispatch(event);
	}
}
