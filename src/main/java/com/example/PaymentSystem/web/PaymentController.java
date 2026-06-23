package com.example.PaymentSystem.web;

import com.example.PaymentSystem.domain.Payment;
import com.example.PaymentSystem.service.PaymentService;
import com.example.PaymentSystem.web.dto.CreatePaymentRequest;
import com.example.PaymentSystem.web.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Initiate a payment (supply Idempotency-Key to make it safe to retry)")
	public PaymentResponse create(
			@Valid @RequestBody CreatePaymentRequest request,
			@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
		return PaymentResponse.from(paymentService.create(request, idempotencyKey));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Fetch a payment by id")
	public PaymentResponse get(@PathVariable String id) {
		return PaymentResponse.from(paymentService.get(id));
	}

	@GetMapping
	@Operation(summary = "List payments")
	public List<PaymentResponse> list() {
		return paymentService.list().stream().map(PaymentResponse::from).toList();
	}

	@PostMapping("/{id}/refunds")
	@Operation(summary = "Refund a successful payment")
	public PaymentResponse refund(@PathVariable String id) {
		return PaymentResponse.from(paymentService.refund(id));
	}
}
