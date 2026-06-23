package com.example.PaymentSystem.domain;

/** Payment lifecycle states. Transitions are enforced in the service layer. */
public enum PaymentStatus {
	INITIATED,
	PENDING,
	SUCCESS,
	FAILED,
	CANCELLED,
	REFUNDED
}
