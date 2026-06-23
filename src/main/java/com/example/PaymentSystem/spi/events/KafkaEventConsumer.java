package com.example.PaymentSystem.spi.events;

import com.example.PaymentSystem.domain.event.PaymentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Bridges Kafka messages back into the shared {@link EventDispatcher} so local
 * handlers behave identically to the in-memory bus. Active only in kafka mode.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.components.event-bus", havingValue = "kafka")
public class KafkaEventConsumer {

	private final EventDispatcher dispatcher;
	private final ObjectMapper objectMapper;

	public KafkaEventConsumer(EventDispatcher dispatcher, ObjectMapper objectMapper) {
		this.dispatcher = dispatcher;
		this.objectMapper = objectMapper;
	}

	@KafkaListener(topics = KafkaEventBus.PAYMENTS_TOPIC, groupId = "${spring.kafka.consumer.group-id:payment-system}")
	public void onMessage(String payload) {
		try {
			PaymentEvent event = objectMapper.readValue(payload, PaymentEvent.class);
			dispatcher.dispatch(event);
		} catch (Exception ex) {
			log.error("Failed to handle Kafka payload: {}", payload, ex);
		}
	}
}
