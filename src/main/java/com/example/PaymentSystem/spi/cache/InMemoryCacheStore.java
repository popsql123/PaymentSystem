package com.example.PaymentSystem.spi.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory substitute for Redis. Thread-safe, with lazy TTL expiry.
 * Active when {@code app.components.cache=memory} (the default).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "app.components.cache", havingValue = "memory", matchIfMissing = true)
public class InMemoryCacheStore implements CacheStore {

	private record Entry(String value, Instant expiresAt) {
		boolean isExpired() {
			return expiresAt != null && Instant.now().isAfter(expiresAt);
		}
	}

	private final ConcurrentHashMap<String, Entry> store = new ConcurrentHashMap<>();

	public InMemoryCacheStore() {
		log.info("CacheStore -> InMemoryCacheStore (Redis substitute)");
	}

	private Entry toEntry(String value, Duration ttl) {
		Instant expiry = (ttl == null || ttl.isZero() || ttl.isNegative()) ? null : Instant.now().plus(ttl);
		return new Entry(value, expiry);
	}

	@Override
	public Optional<String> get(String key) {
		Entry entry = store.compute(key, (k, existing) -> (existing != null && existing.isExpired()) ? null : existing);
		return Optional.ofNullable(entry).map(Entry::value);
	}

	@Override
	public void put(String key, String value, Duration ttl) {
		store.put(key, toEntry(value, ttl));
	}

	@Override
	public boolean putIfAbsent(String key, String value, Duration ttl) {
		Entry desired = toEntry(value, ttl);
		Entry result = store.compute(key, (k, existing) -> {
			if (existing == null || existing.isExpired()) {
				return desired;
			}
			return existing;
		});
		return result == desired;
	}

	@Override
	public void evict(String key) {
		store.remove(key);
	}
}
