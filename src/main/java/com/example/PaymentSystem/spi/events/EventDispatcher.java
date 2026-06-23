package com.example.PaymentSystem.spi.events;

import com.example.PaymentSystem.domain.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Shared fan-out logic: routes a {@link DomainEvent} to every {@link EventHandler}
 * that supports it. Used by both the in-memory and Kafka event buses so the
 * subscriber side is identical across backends.
 */
@Slf4j
@Component
public class EventDispatcher {

	private final List<EventHandler> handlers;

	public EventDispatcher(List<EventHandler> handlers) {
		this.handlers = handlers;
	}

	public void dispatch(DomainEvent event) {
		for (EventHandler handler : handlers) {
			if (handler.supports(event.type())) {
				try {
					handler.handle(event);
				} catch (Exception ex) {
					log.error("Handler {} failed for event {}", handler.getClass().getSimpleName(), event.type(), ex);
				}
			}
		}
	}
}
