package com.example.PaymentSystem.spi.cache;

import java.time.Duration;
import java.util.Optional;

/**
 * Port for a key/value cache. Implemented by an in-memory map and by Redis.
 * The active implementation is chosen via {@code app.components.cache}.
 */
public interface CacheStore {

	Optional<String> get(String key);

	void put(String key, String value, Duration ttl);

	/** Atomic set-if-absent. Returns true when the value was stored (key was free). */
	boolean putIfAbsent(String key, String value, Duration ttl);

	void evict(String key);
}
