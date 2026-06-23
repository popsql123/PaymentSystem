package com.example.PaymentSystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Selects which backing implementation each infrastructure component uses.
 *
 * <p>Each value is a simple string so it can drive {@code @ConditionalOnProperty}
 * on the adapter beans. Defaults are the in-memory substitutes, so the whole
 * application boots and runs with zero external infrastructure.
 *
 * <pre>
 * app.components.persistence = memory | jpa      (jpa  -> H2 / any JDBC)
 * app.components.cache       = memory | redis
 * app.components.event-bus   = memory | kafka
 * </pre>
 */
@Component
@Data
@ConfigurationProperties(prefix = "app.components")
public class ComponentsProperties {

	/** memory | jpa */
	private String persistence = "memory";

	/** memory | redis */
	private String cache = "memory";

	/** memory | kafka */
	private String eventBus = "memory";
}
