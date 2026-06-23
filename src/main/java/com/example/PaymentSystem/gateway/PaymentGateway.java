package com.example.PaymentSystem.gateway;

import com.example.PaymentSystem.domain.Payment;

/**
 * Abstraction over the external (downstream) payment processor. A real adapter
 * would call Stripe/Razorpay/etc.; the bundled implementation is a mock so the
 * project runs end-to-end with no third party.
 */
public interface PaymentGateway {

	GatewayResult charge(Payment payment, String simulateOutcome);

	GatewayResult refund(Payment payment);
}
