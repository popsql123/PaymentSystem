package com.example.PaymentSystem.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Spring Data JPA repository backing the JPA persistence adapter. */
public interface SpringDataPaymentRepository extends JpaRepository<PaymentEntity, String> {

	Optional<PaymentEntity> findByIdempotencyKey(String idempotencyKey);
}
