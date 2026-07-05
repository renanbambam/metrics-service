# metrics-service

A transactions API with a full observability stack: custom metrics, Prometheus, Grafana.

I built this to fill a gap in my own experience. I'd used Actuator before, but always with the default metrics — never wired up custom counters and timers end to end, and never touched Prometheus or Grafana beyond reading someone else's dashboard. So I picked a small domain (transactions) as an excuse to build the whole pipeline myself: code emits metrics, Actuator exposes them, Prometheus scrapes, Grafana renders.

The annoying part was the AOP + Micrometer interaction. My first version of `MetricsAspect` wrapped `ProceedingJoinPoint.proceed()` inside `Timer.record(Supplier)`, and any checked exception from the domain got swallowed and rethrown as a generic `RuntimeException`, which meant `GlobalExceptionHandler` couldn't tell an `IllegalArgumentException` apart from anything else anymore. Switched to measuring elapsed time manually with `Instant.now()` around a try/finally and recording failures in the catch block — less elegant than the Micrometer helper, but it keeps the original exception type intact.

## How it works

The core is a hexagonal setup: `domain` has no Spring/JPA/Micrometer imports at all, only `TransactionService` implementing two inbound ports (`CreateTransactionPort`, `ListTransactionsPort`) against two outbound ports (`TransactionRepository`, `TransactionMetricsPort`). The REST adapter and the JPA adapter each depend on the domain, never the other way around.

`TransactionMetrics` (in `observability/`) is the concrete implementation of `TransactionMetricsPort` and owns the actual Micrometer objects — a `Counter` for totals, one for failures, a `Timer` with p50/p95/p99, and a `DistributionSummary` for the amounts. `MetricsAspect` wraps every call to `TransactionService` with `@Around` advice, so timing and failure-counting happen without the service itself knowing it's being measured.

Prometheus scrapes `/actuator/prometheus` every 15s. Grafana has both the datasource and the "Transactions" dashboard provisioned from files, so there's nothing to click through manually on first boot.

## Stack

Java 17, Spring Boot 3.2, Micrometer, PostgreSQL, Prometheus, Grafana, Docker Compose.

## Running it

Full stack, with Postgres/Prometheus/Grafana:

    docker compose -f observability/docker-compose.yml up -d

Give it about 20 seconds, then:

- API: http://localhost:8080
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin) — the "Transactions" dashboard is already there

Just the API, against a local Postgres:

    mvn spring-boot:run

### Generating some load

    for i in $(seq 1 50); do
      curl -s -X POST http://localhost:8080/api/v1/transactions \
        -H "Content-Type: application/json" \
        -d '{"accountId":"acc-1","amount":100.00,"type":"DEBIT"}' > /dev/null
    done

### Custom metrics

| Metric | Type | What it measures |
|---|---|---|
| `transactions_total` | Counter | transactions processed successfully |
| `transactions_failed_total` | Counter | failures, tagged by exception class |
| `transactions_processing_time_seconds` | Timer | latency with p50/p95/p99 |
| `transactions_amount` | Summary | distribution of transaction amounts |

## Known limitations

- No auth on `/actuator/prometheus` or on the transactions endpoints. Fine for a local stack, not something I'd ship to a shared environment as-is.
- No alerting rules in Prometheus — the dashboard is there to look at, nothing pages anyone.
- Single app instance. I haven't dealt with what scraping looks like across replicas (`instance` label dedup, HA Prometheus, etc).
- `ddl-auto: validate` expects the schema to already exist, so I added a plain `schema.sql` instead of pulling in Flyway or Liquibase for one table. That won't hold up once there's more than one table or an actual migration to write.
- The failure counter tags by exception class name, not by a stable business reason code — `IllegalArgumentException` tells you something failed, not why.
- No idempotency key on transaction creation. Retry a POST and you get two transactions.

## What I'd do differently

Given more time, I'd add a `reason` enum to `TransactionMetricsPort.recordFailure` instead of passing the exception's simple name straight through — right now the dashboard can show *that* something failed but not distinguish validation errors from actual downstream failures at a glance.

## Tests

    mvn test

`TransactionServiceTest` mocks both ports with Mockito. `TransactionControllerTest` is a `@WebMvcTest` slice — no database involved. `TransactionMetricsTest` uses Micrometer's `SimpleMeterRegistry` directly instead of mocking Micrometer's own classes.
