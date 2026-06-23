package com.example.PaymentSystem.service;

import com.example.PaymentSystem.domain.Payment;
import com.example.PaymentSystem.domain.PaymentMethod;
import com.example.PaymentSystem.domain.PaymentStatus;
import com.example.PaymentSystem.domain.event.DomainEvent;
import com.example.PaymentSystem.gateway.MockPaymentGateway;
import com.example.PaymentSystem.persistence.memory.InMemoryPaymentRepository;
import com.example.PaymentSystem.spi.cache.InMemoryCacheStore;
import com.example.PaymentSystem.spi.events.EventBus;
import com.example.PaymentSystem.web.dto.CreatePaymentRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pure unit test wiring the in-memory component implementations directly — no
 * Spring context, no external infrastructure. Proves the service works against
 * the in-memory substitutes.
 */
class PaymentServiceTest {

	private final List<DomainEvent> published = new ArrayList<>();
	private final EventBus recordingBus = published::add;

	private PaymentService newService() {
		return new PaymentService(
				new InMemoryPaymentRepository(),
				new InMemoryCacheStore(),
				recordingBus,
				new MockPaymentGateway());
	}

	private CreatePaymentRequest request(String outcome) {
		return new CreatePaymentRequest("order-1", new BigDecimal("99.50"), "INR", PaymentMethod.UPI, outcome);
	}

	@Test
	void createsSuccessfulPaymentAndPublishesEvent() {
		PaymentService service = newService();
		Payment payment = service.create(request(null), null);

		assertNotNull(payment.getId());
		assertEquals(PaymentStatus.SUCCESS, payment.getStatus());
		assertNotNull(payment.getGatewayRef());
		assertEquals(1, published.size());
		assertEquals("payment.success", published.get(0).type());
	}

	@Test
	void idempotentKeyReturnsSamePayment() {
		PaymentService service = newService();
		Payment first = service.create(request(null), "key-123");
		Payment second = service.create(request(null), "key-123");

		assertEquals(first.getId(), second.getId());
		// Only the first call should have produced an event.
		assertEquals(1, published.size());
	}

	@Test
	void refundOnlyAllowedForSuccess() {
		PaymentService service = newService();
		Payment failed = service.create(request("FAILED"), null);
		assertEquals(PaymentStatus.FAILED, failed.getStatus());
		assertThrows(IllegalStateException.class, () -> service.refund(failed.getId()));

		Payment ok = service.create(request("SUCCESS"), null);
		Payment refunded = service.refund(ok.getId());
		assertEquals(PaymentStatus.REFUNDED, refunded.getStatus());
	}

	@Test
	void getMissingThrowsNotFound() {
		PaymentService service = newService();
		assertThrows(NotFoundException.class, () -> service.get("nope"));
	}
}
