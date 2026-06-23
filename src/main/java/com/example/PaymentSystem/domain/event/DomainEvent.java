package com.example.PaymentSystem.domain.event;

import java.time.Instant;

/** Base contract for anything published through the {@code EventBus} port. */
public interface DomainEvent {

	String type();

	String aggregateId();

	Instant occurredAt();
}
