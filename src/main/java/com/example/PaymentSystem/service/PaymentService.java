package com.example.PaymentSystem.service;

import com.example.PaymentSystem.domain.Payment;
import com.example.PaymentSystem.domain.PaymentStatus;
import com.example.PaymentSystem.domain.event.PaymentEvent;
import com.example.PaymentSystem.gateway.GatewayResult;
import com.example.PaymentSystem.gateway.PaymentGateway;
import com.example.PaymentSystem.persistence.PaymentRepository;
import com.example.PaymentSystem.spi.cache.CacheStore;
import com.example.PaymentSystem.spi.events.EventBus;
import com.example.PaymentSystem.web.dto.CreatePaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Orchestrates the payment flow. Note how it only depends on the three ports
 * ({@link PaymentRepository}, {@link CacheStore}, {@link EventBus}) plus the
 * {@link PaymentGateway} — it has no idea whether it is talking to memory,
 * Redis, Kafka or H2. That is what makes the components swappable by config.
 */
@Slf4j
@Service
public class PaymentService {

	private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);
	private static final String IDEMPOTENCY_PREFIX = "idem:payment:";

	private final PaymentRepository repository;
	private final CacheStore cache;
	private final EventBus eventBus;
	private final PaymentGateway gateway;

	public PaymentService(PaymentRepository repository, CacheStore cache, EventBus eventBus, PaymentGateway gateway) {
		this.repository = repository;
		this.cache = cache;
		this.eventBus = eventBus;
		this.gateway = gateway;
	}

	public Payment create(CreatePaymentRequest request, String idempotencyKey) {
		// 1. Idempotency fast-path (cache) then durable check (repository).
		if (idempotencyKey != null && !idempotencyKey.isBlank()) {
			Optional<String> cachedId = cache.get(IDEMPOTENCY_PREFIX + idempotencyKey);
			if (cachedId.isPresent()) {
				Optional<Payment> existing = repository.findById(cachedId.get());
				if (existing.isPresent()) {
					log.info("Idempotent replay (cache hit) for key {}", idempotencyKey);
					return existing.get();
				}
			}
			Optional<Payment> durable = repository.findByIdempotencyKey(idempotencyKey);
			if (durable.isPresent()) {
				cache.put(IDEMPOTENCY_PREFIX + idempotencyKey, durable.get().getId(), IDEMPOTENCY_TTL);
				return durable.get();
			}
		}

		// 2. Create the payment in INITIATED state.
		Instant now = Instant.now();
		Payment payment = Payment.builder()
				.id(UUID.randomUUID().toString())
				.orderId(request.orderId())
				.amount(request.amount())
				.currency(request.currency().toUpperCase())
				.method(request.method())
				.status(PaymentStatus.INITIATED)
				.idempotencyKey(idempotencyKey)
				.createdAt(now)
				.updatedAt(now)
				.build();
		repository.save(payment);

		// 3. Call the downstream gateway and apply the outcome.
		GatewayResult result = gateway.charge(payment, request.simulateOutcome());
		payment.setGatewayRef(result.gatewayRef());
		payment.setStatus(result.status());
		payment.setUpdatedAt(Instant.now());
		repository.save(payment);

		// 4. Record idempotency mapping + publish the result event.
		if (idempotencyKey != null && !idempotencyKey.isBlank()) {
			cache.put(IDEMPOTENCY_PREFIX + idempotencyKey, payment.getId(), IDEMPOTENCY_TTL);
		}
		eventBus.publish(PaymentEvent.of(payment, "payment." + payment.getStatus().name().toLowerCase()));
		return payment;
	}

	public Payment get(String id) {
		return repository.findById(id)
				.orElseThrow(() -> new NotFoundException("Payment not found: " + id));
	}

	public List<Payment> list() {
		return repository.findAll();
	}

	public Payment refund(String id) {
		Payment payment = get(id);
		if (payment.getStatus() != PaymentStatus.SUCCESS) {
			throw new IllegalStateException(
					"Only SUCCESS payments can be refunded; current status is " + payment.getStatus());
		}
		GatewayResult result = gateway.refund(payment);
		payment.setStatus(result.status());
		payment.setUpdatedAt(Instant.now());
		repository.save(payment);
		eventBus.publish(PaymentEvent.of(payment, "payment.refunded"));
		return payment;
	}
}
