package com.example.PaymentSystem.analytics;

import com.example.PaymentSystem.domain.event.DomainEvent;
import com.example.PaymentSystem.spi.events.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * Builds a tiny read model from the payment event stream: a count per event
 * type. Demonstrates the CQRS-style projection regardless of whether events
 * arrive via the in-memory bus or Kafka.
 */
@Slf4j
@Component
public class AnalyticsEventHandler implements EventHandler {

	private final ConcurrentHashMap<String, LongAdder> countsByType = new ConcurrentHashMap<>();

	@Override
	public boolean supports(String eventType) {
		return eventType != null && eventType.startsWith("payment.");
	}

	@Override
	public void handle(DomainEvent event) {
		countsByType.computeIfAbsent(event.type(), k -> new LongAdder()).increment();
		log.debug("Analytics projected {}", event.type());
	}

	public Map<String, Long> snapshot() {
		Map<String, Long> out = new ConcurrentHashMap<>();
		countsByType.forEach((k, v) -> out.put(k, v.sum()));
		return out;
	}
}
