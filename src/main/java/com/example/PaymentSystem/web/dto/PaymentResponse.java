package com.example.PaymentSystem.web.dto;

import com.example.PaymentSystem.domain.Payment;
import com.example.PaymentSystem.domain.PaymentMethod;
import com.example.PaymentSystem.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
		String id,
		String orderId,
		BigDecimal amount,
		String currency,
		PaymentMethod method,
		PaymentStatus status,
		String gatewayRef,
		Instant createdAt,
		Instant updatedAt
) {
	public static PaymentResponse from(Payment p) {
		return new PaymentResponse(
				p.getId(), p.getOrderId(), p.getAmount(), p.getCurrency(), p.getMethod(),
				p.getStatus(), p.getGatewayRef(), p.getCreatedAt(), p.getUpdatedAt());
	}
}
