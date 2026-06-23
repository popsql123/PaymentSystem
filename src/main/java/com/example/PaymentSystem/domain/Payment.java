package com.example.PaymentSystem.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Persistence-agnostic domain model. The persistence adapters map this to/from
 * their own storage representation (a JPA entity, or a plain map entry).
 */
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	private String id;
	private String orderId;
	private BigDecimal amount;
	private String currency;
	private PaymentMethod method;
	private PaymentStatus status;
	private String gatewayRef;
	private String idempotencyKey;
	private Instant createdAt;
	private Instant updatedAt;
}
