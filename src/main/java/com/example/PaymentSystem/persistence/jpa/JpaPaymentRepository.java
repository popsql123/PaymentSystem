package com.example.PaymentSystem.persistence.jpa;

import com.example.PaymentSystem.domain.Payment;
import com.example.PaymentSystem.persistence.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA/H2-backed persistence adapter. Active when
 * {@code app.components.persistence=jpa}. Maps between the domain model and the
 * JPA entity so the rest of the app never depends on JPA types.
 */
@Slf4j
@Repository
@ConditionalOnProperty(name = "app.components.persistence", havingValue = "jpa")
public class JpaPaymentRepository implements PaymentRepository {

	private final SpringDataPaymentRepository delegate;

	public JpaPaymentRepository(SpringDataPaymentRepository delegate) {
		this.delegate = delegate;
		log.info("PaymentRepository -> JpaPaymentRepository (H2/JDBC)");
	}

	@Override
	public Payment save(Payment payment) {
		return toDomain(delegate.save(toEntity(payment)));
	}

	@Override
	public Optional<Payment> findById(String id) {
		return delegate.findById(id).map(this::toDomain);
	}

	@Override
	public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
		if (idempotencyKey == null) {
			return Optional.empty();
		}
		return delegate.findByIdempotencyKey(idempotencyKey).map(this::toDomain);
	}

	@Override
	public List<Payment> findAll() {
		return delegate.findAll().stream().map(this::toDomain).toList();
	}

	private PaymentEntity toEntity(Payment p) {
		return new PaymentEntity(
				p.getId(), p.getOrderId(), p.getAmount(), p.getCurrency(), p.getMethod(),
				p.getStatus(), p.getGatewayRef(), p.getIdempotencyKey(), p.getCreatedAt(), p.getUpdatedAt());
	}

	private Payment toDomain(PaymentEntity e) {
		return Payment.builder()
				.id(e.getId())
				.orderId(e.getOrderId())
				.amount(e.getAmount())
				.currency(e.getCurrency())
				.method(e.getMethod())
				.status(e.getStatus())
				.gatewayRef(e.getGatewayRef())
				.idempotencyKey(e.getIdempotencyKey())
				.createdAt(e.getCreatedAt())
				.updatedAt(e.getUpdatedAt())
				.build();
	}
}
