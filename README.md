# PayOne — Payments, Subscriptions & Analytics Platform

A Spring Boot payment platform built around **pluggable infrastructure components**.
Every external dependency — the database (H2), the cache (Redis) and the message
bus (Kafka) — sits behind a port (interface) with **two interchangeable adapters**:
an in-memory substitute and the real backend. Which one is wired in is decided
entirely by configuration, and the defaults are **all in-memory**, so the app
boots and runs end-to-end with zero external infrastructure.

```
app:
  components:
    persistence: memory   # memory | jpa     (jpa -> H2 / any JDBC)
    cache:       memory   # memory | redis
    event-bus:   memory   # memory | kafka
```

## Why this design

The service layer depends only on three ports plus the gateway:

| Port | In-memory adapter | Real adapter | Toggle |
|------|-------------------|--------------|--------|
| `PaymentRepository` | `InMemoryPaymentRepository` (ConcurrentHashMap) | `JpaPaymentRepository` (H2/JPA) | `app.components.persistence` |
| `CacheStore` | `InMemoryCacheStore` (map + TTL) | `RedisCacheStore` (StringRedisTemplate) | `app.components.cache` |
| `EventBus` | `InMemoryEventBus` (in-process fan-out) | `KafkaEventBus` (+ `KafkaEventConsumer`) | `app.components.event-bus` |

The adapters are selected with `@ConditionalOnProperty`, so only the chosen bean
is created. In the default (all-memory) mode, no Redis/Kafka connection is ever
opened.

## Run it

```bash
./mvnw spring-boot:run        # all in-memory, nothing else needed
```

Then open:
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health:     http://localhost:8080/actuator/health

### Switch components on

```bash
# Use real Redis for the cache
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.components.cache=redis

# Or via the convenience profiles
./mvnw spring-boot:run -Dspring-boot.run.profiles=redis,kafka,jpa
```

Environment overrides: `REDIS_HOST`, `REDIS_PORT`, `KAFKA_BOOTSTRAP_SERVERS`.
When `persistence=jpa`, the H2 console is at http://localhost:8080/h2-console
(JDBC URL `jdbc:h2:mem:payone`).

## API (v1)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/payments` | Initiate a payment. Send an `Idempotency-Key` header to make retries safe. |
| GET  | `/api/v1/payments/{id}` | Fetch a payment. |
| GET  | `/api/v1/payments` | List payments. |
| POST | `/api/v1/payments/{id}/refunds` | Refund a successful payment. |
| GET  | `/api/v1/analytics/payments` | Event-stream projection (counts per event type). |

Example:

```bash
curl -X POST http://localhost:8080/api/v1/payments \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: demo-1' \
  -d '{"orderId":"order-1","amount":99.50,"currency":"INR","method":"UPI"}'
```

Force a failure (sandbox): add `"simulateOutcome":"FAILED"` (or `PENDING`).

## Idempotency flow

1. Fast-path lookup in `CacheStore` (`idem:payment:<key>` -> paymentId).
2. Durable fallback via `PaymentRepository.findByIdempotencyKey`.
3. On first processing, the key->id mapping is cached (24h TTL) and a
   `payment.*` event is published exactly once.

## Project layout

```
config/       component toggles, security, OpenAPI, startup banner
domain/       Payment model, enums, events
gateway/      PaymentGateway port + MockPaymentGateway
persistence/  PaymentRepository port + memory & jpa adapters
spi/cache/    CacheStore port + memory & redis adapters
spi/events/   EventBus port + memory & kafka adapters, shared dispatcher
service/      PaymentService (depends only on ports)
analytics/    event-stream projection + endpoint
web/          controllers, DTOs, error handling
```

## Tests

`PaymentServiceTest` wires the in-memory adapters directly (no Spring context)
and covers the happy path, idempotent replay, refund rules and not-found.

```bash
./mvnw test
```

## Notes

- H2 is a dev/local convenience. The persistence layer is JDBC-agnostic — point
  `spring.datasource.*` at Postgres/MySQL for production.
- This repo implements the **core payments slice** of the full platform spec
  (payments, idempotency, refunds, events, analytics projection). Subscriptions,
  multi-tenancy, webhooks, fraud, and the ledger are designed for in the spec
  and slot into the same ports.
