package com.example.PaymentSystem.persistence.memory;

import com.example.PaymentSystem.domain.Payment;
import com.example.PaymentSystem.persistence.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory substitute for the H2/JPA store. Active when
 * {@code app.components.persistence=memory} (the default).
 */
@Slf4j
@Repository
@ConditionalOnProperty(name = "app.components.persistence", havingValue = "memory", matchIfMissing = true)
public class InMemoryPaymentRepository implements PaymentRepository {

	private final ConcurrentHashMap<String, Payment> store = new ConcurrentHashMap<>();

	public InMemoryPaymentRepository() {
		log.info("PaymentRepository -> InMemoryPaymentRepository (H2/JPA substitute)");
	}

	@Override
	public Payment save(Payment payment) {
		store.put(payment.getId(), payment);
		return payment;
	}

	@Override
	public Optional<Payment> findById(String id) {
		return Optional.ofNullable(store.get(id));
	}

	@Override
	public Optional<Payment> findByIdempotencyKey(String idempotencyKey) {
		if (idempotencyKey == null) {
			return Optional.empty();
		}
		return store.values().stream()
				.filter(p -> idempotencyKey.equals(p.getIdempotencyKey()))
				.findFirst();
	}

	@Override
	public List<Payment> findAll() {
		return new ArrayList<>(store.values());
	}
}
