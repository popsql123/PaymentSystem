package com.example.PaymentSystem.spi.events;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declares the payments topic. Only present in kafka mode, which also means the
 * auto-configured KafkaAdmin stays idle (no broker connection) in memory mode.
 */
@Configuration
@ConditionalOnProperty(name = "app.components.event-bus", havingValue = "kafka")
public class KafkaTopicConfig {

	@Bean
	public NewTopic paymentsTopic() {
		return TopicBuilder.name(KafkaEventBus.PAYMENTS_TOPIC).partitions(3).replicas(1).build();
	}
}
