package com.example.PaymentSystem.persistence;

import com.example.PaymentSystem.domain.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Port for persisting payments. Implemented by an in-memory map and by JPA/H2.
 * The active implementation is chosen via {@code app.components.persistence}.
 */
public interface PaymentRepository {

	Payment save(Payment payment);

	Optional<Payment> findById(String id);

	Optional<Payment> findByIdempotencyKey(String idempotencyKey);

	List<Payment> findAll();
}
