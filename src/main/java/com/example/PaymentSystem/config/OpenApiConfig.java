package com.example.PaymentSystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI payOneOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("PayOne API")
				.version("v1")
				.description("Payments, subscriptions & analytics platform. "
						+ "Infrastructure components (persistence, cache, event-bus) are pluggable "
						+ "via app.components.* and default to in-memory substitutes.")
				.license(new License().name("MIT")));
	}
}
