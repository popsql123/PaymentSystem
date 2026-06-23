//package com.example.PaymentSystem.spi.cache;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.util.Optional;
//
///**
// * Redis-backed cache. Active when {@code app.components.cache=redis}.
// * Relies on the Spring Boot auto-configured {@link StringRedisTemplate}.
// */
//@Slf4j
//@Component
//@ConditionalOnProperty(name = "app.components.cache", havingValue = "redis")
//public class RedisCacheStore implements CacheStore {
//
//	private final StringRedisTemplate redis;
//
//	public RedisCacheStore(StringRedisTemplate redis) {
//		this.redis = redis;
//		log.info("CacheStore -> RedisCacheStore");
//	}
//
//	@Override
//	public Optional<String> get(String key) {
//		return Optional.ofNullable(redis.opsForValue().get(key));
//	}
//
//	@Override
//	public void put(String key, String value, Duration ttl) {
//		if (ttl == null || ttl.isZero() || ttl.isNegative()) {
//			redis.opsForValue().set(key, value);
//		} else {
//			redis.opsForValue().set(key, value, ttl);
//		}
//	}
//
//	@Override
//	public boolean putIfAbsent(String key, String value, Duration ttl) {
//		Boolean stored = (ttl == null || ttl.isZero() || ttl.isNegative())
//				? redis.opsForValue().setIfAbsent(key, value)
//				: redis.opsForValue().setIfAbsent(key, value, ttl);
//		return Boolean.TRUE.equals(stored);
//	}
//
//	@Override
//	public void evict(String key) {
//		redis.delete(key);
//	}
//}
