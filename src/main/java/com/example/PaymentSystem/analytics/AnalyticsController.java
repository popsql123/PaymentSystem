package com.example.PaymentSystem.analytics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics")
public class AnalyticsController {

	private final AnalyticsEventHandler analytics;

	public AnalyticsController(AnalyticsEventHandler analytics) {
		this.analytics = analytics;
	}

	@GetMapping("/payments")
	@Operation(summary = "Payment event counts projected from the event stream")
	public Map<String, Long> payments() {
		return analytics.snapshot();
	}
}
