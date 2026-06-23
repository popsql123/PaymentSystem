package com.example.PaymentSystem.persistence.jpa;

import com.example.PaymentSystem.domain.PaymentMethod;
import com.example.PaymentSystem.domain.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments", indexes = {
		@Index(name = "idx_payments_idempotency_key", columnList = "idempotencyKey")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {

	@Id
	private String id;

	private String orderId;

	@Column(precision = 19, scale = 4)
	private BigDecimal amount;

	private String currency;

	@Enumerated(EnumType.STRING)
	private PaymentMethod method;

	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	private String gatewayRef;

	@Column(unique = true)
	private String idempotencyKey;

	private Instant createdAt;

	private Instant updatedAt;
}
