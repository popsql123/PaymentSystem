package com.example.PaymentSystem.spi.events;

import com.example.PaymentSystem.domain.event.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.PaymentSystem.domain.event.DomainEvent;

/**
 * Kafka-backed event bus. Active when {@code app.components.event-bus=kafka}.
 * Serializes events to JSON and publishes them keyed by aggregate id (for
 * per-aggregate ordering). Consumption is handled by {@link KafkaEventConsumer}.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.components.event-bus", havingValue = "kafka")
public class KafkaEventBus implements EventBus {

	public static final String PAYMENTS_TOPIC = "payments.events";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public KafkaEventBus(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
		log.info("EventBus -> KafkaEventBus (topic={})", PAYMENTS_TOPIC);
	}

	@Override
	public void publish(DomainEvent event) {
		try {
			// Only PaymentEvent exists today; serialize concretely so the consumer can deserialize.
			String payload = objectMapper.writeValueAsString((PaymentEvent) event);
			kafkaTemplate.send(PAYMENTS_TOPIC, event.aggregateId(), payload);
		} catch (Exception ex) {
			throw new IllegalStateException("Failed to publish event " + event.type(), ex);
		}
	}
}
