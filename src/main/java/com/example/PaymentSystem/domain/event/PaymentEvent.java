package com.example.PaymentSystem.domain.event;

import com.example.PaymentSystem.domain.Payment;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Emitted whenever a payment changes state. Carries enough data for downstream
 * consumers (analytics, notifications) without needing to load the aggregate.
 */
public record PaymentEvent(
		String type,
		String aggregateId,
		String status,
		BigDecimal amount,
		String currency,
		Instant occurredAt
) implements DomainEvent {

	public static PaymentEvent of(Payment payment, String type) {
		return new PaymentEvent(
				type,
				payment.getId(),
				payment.getStatus() == null ? null : payment.getStatus().name(),
				payment.getAmount(),
				payment.getCurrency(),
				Instant.now());
	}
}
