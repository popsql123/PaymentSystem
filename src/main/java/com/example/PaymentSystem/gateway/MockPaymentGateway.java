package com.example.PaymentSystem.gateway;

import com.example.PaymentSystem.domain.Payment;
import com.example.PaymentSystem.domain.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Deterministic mock gateway. Defaults to SUCCESS, but callers can force an
 * outcome via {@code simulateOutcome} (SUCCESS | FAILED | PENDING) which is
 * handy for the sandbox and for exercising the refund/failure paths.
 */
@Slf4j
@Component
public class MockPaymentGateway implements PaymentGateway {

	@Override
	public GatewayResult charge(Payment payment, String simulateOutcome) {
		PaymentStatus status = resolve(simulateOutcome);
		String ref = "gw_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		log.info("MockGateway charge {} {} -> {} ({})", payment.getAmount(), payment.getCurrency(), status, ref);
		return new GatewayResult(status, ref);
	}

	@Override
	public GatewayResult refund(Payment payment) {
		String ref = "rf_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		log.info("MockGateway refund {} -> {}", payment.getId(), ref);
		return new GatewayResult(PaymentStatus.REFUNDED, ref);
	}

	private PaymentStatus resolve(String simulateOutcome) {
		if (simulateOutcome == null || simulateOutcome.isBlank()) {
			return PaymentStatus.SUCCESS;
		}
		try {
			PaymentStatus requested = PaymentStatus.valueOf(simulateOutcome.trim().toUpperCase());
			return switch (requested) {
				case SUCCESS, FAILED, PENDING -> requested;
				default -> PaymentStatus.SUCCESS;
			};
		} catch (IllegalArgumentException ex) {
			return PaymentStatus.SUCCESS;
		}
	}
}
