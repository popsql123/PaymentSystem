package com.example.PaymentSystem.gateway;

import com.example.PaymentSystem.domain.PaymentStatus;

/** Outcome returned by the downstream payment gateway. */
public record GatewayResult(PaymentStatus status, String gatewayRef) {
}
