package com.example.PaymentSystem.web.dto;

import com.example.PaymentSystem.domain.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePaymentRequest(
		@NotBlank String orderId,
		@NotNull @Positive BigDecimal amount,
		@NotBlank String currency,
		@NotNull PaymentMethod method,
		/** Optional sandbox hint: SUCCESS | FAILED | PENDING. Defaults to SUCCESS. */
		String simulateOutcome
) {
}
