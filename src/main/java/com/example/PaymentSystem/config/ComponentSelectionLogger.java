package com.example.PaymentSystem.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/** Logs the selected component backends at startup so the active wiring is obvious. */
@Slf4j
@Component
@RequiredArgsConstructor
public class ComponentSelectionLogger implements ApplicationRunner {

	private final ComponentsProperties components;

	@Override
	public void run(ApplicationArguments args) {
		log.info("""

				=====================================================
			 PayOne component wiring (app.components.*)
			   persistence : {}
			   cache       : {}
			   event-bus   : {}
			=====================================================""",
				components.getPersistence(),
				components.getCache(),
				components.getEventBus());
	}
}
