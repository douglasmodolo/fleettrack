# order-service

Responsible for managing delivery orders within the FleetTrack logistics system. This service is the entry point of the delivery flow — orders are created here, their status is updated as the delivery progresses, and drivers are assigned.

---

## Responsibilities

- Create and manage delivery orders
- Validate and enforce order status transitions
- Assign drivers to orders
- Publish events when order status changes (upcoming: Kafka integration)

---

## Architecture

This service follows **Clean Architecture**, organized into four layers:

```
presentation/     ← REST controllers, DTOs, input validation
application/      ← Use cases and ports (interfaces)
domain/           ← Business rules, entities, domain exceptions
infrastructure/   ← JPA persistence, database adapters
```

The core rule: **inner layers never depend on outer layers.** The domain has no knowledge of Spring, JPA, or HTTP. If the database is swapped tomorrow, the domain doesn't change.

### Key design decisions

**Value Object for Address**
`Address` has no identity of its own — it's defined by its values. It's stored in the same table as the order via JPA `@Embedded`, with no join and no extra table. The `complement` field returns `Optional<String>` to force callers to handle absence explicitly.

**Factory methods on Order**
`Order.create()` is the only way to create a new order. It enforces initialization rules: status always starts as `PENDING`, dates are set here, never by the caller. `Order.reconstitute()` is used exclusively by the infrastructure layer to rebuild an order from the database without applying business rules.

**No public setters on Order**
State only changes through business methods: `updateStatus()` and `assignDriver()`. This is real encapsulation — no one can put the entity in an invalid state accidentally.

**Status transition validation in the domain**
`Order.updateStatus()` enforces which transitions are allowed. The rule lives in the domain so any layer calling this method is automatically protected — no duplication across use cases.

Allowed transitions:
```
PENDING    → PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED
PICKED_UP  → IN_TRANSIT, DELIVERED, CANCELLED
IN_TRANSIT → DELIVERED, CANCELLED
DELIVERED  → (final state)
CANCELLED  → (final state)
```

Forward skips are allowed (e.g. PENDING → DELIVERED for operational flexibility). Backwards transitions are always rejected. Invalid transitions return `422 Unprocessable Entity`.

**Ports and Adapters**
Use cases depend on interfaces (`OrderRepositoryPort`), never on JPA directly. The infrastructure implements those interfaces. This makes the domain testable in isolation — no Spring context needed.

**Optimistic Locking**
`OrderJpaEntity` has a `@Version` field managed by Hibernate. If two transactions try to update the same order concurrently, the second one receives an `OptimisticLockingFailureException`. The `version` field is carried through the domain object to ensure correct merge behavior.

**Separate JPA entity**
`OrderJpaEntity` and `AddressEmbeddable` live in the infrastructure layer. The domain `Order` and `Address` have no JPA annotations — they're plain Java. `OrderMapper` handles conversion between the two representations.

**Schema versioning with Flyway**
Database schema is managed by Flyway migrations in `src/main/resources/db/migration/`. The Hibernate `ddl-auto` is set to `validate` — it verifies the schema matches the entities but never modifies it. All schema changes must go through a versioned migration file.

---

## Endpoints

| Method | Path | Description | Status |
|--------|------|-------------|--------|
| `POST` | `/orders` | Create a new order | `201 Created` |
| `GET` | `/orders/{id}` | Get order by ID | `200 OK` |
| `GET` | `/orders` | List orders (paginated) | `200 OK` |
| `PATCH` | `/orders/{id}/status` | Update order status | `200 OK` |
| `PATCH` | `/orders/{id}/driver` | Assign driver to order | `200 OK` |

### POST /orders

**Request body:**
```json
{
  "customerName": "Douglas Silva",
  "originAddress": {
    "country": "Brasil",
    "state": "SP",
    "city": "São Paulo",
    "zipCode": "01310-100",
    "street": "Avenida Paulista",
    "number": "1000"
  },
  "destinationAddress": {
    "country": "Brasil",
    "state": "SP",
    "city": "Campinas",
    "zipCode": "13010-100",
    "street": "Rua Conceição",
    "number": "250",
    "complement": "Apto 42"
  }
}
```

**Response `201 Created`:**
```json
{
  "id": "92d3d14a-70dd-45b3-97f4-aa4257c91b61",
  "status": "PENDING",
  "createdAt": "2026-05-13T19:17:47.386591",
  "estimatedDeliveryAt": "2026-05-16T19:17:47.386591",
  "destinationAddress": {
    "country": "Brasil",
    "state": "SP",
    "city": "Campinas",
    "zipCode": "13010-100",
    "street": "Rua Conceição",
    "number": "250",
    "complement": "Apto 42"
  }
}
```

### GET /orders

Supports pagination via query params:

```
GET /orders?page=0&size=10
```

**Response `200 OK`:**
```json
{
  "content": [ ... ],
  "totalElements": 3,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### PATCH /orders/{id}/status

**Request body:**
```json
{
  "status": "PICKED_UP"
}
```

Invalid transitions return `422 Unprocessable Entity`:
```json
{
  "status": 422,
  "message": "Invalid status transition from DELIVERED to PENDING",
  "timestamp": "2026-05-13T19:26:32.859",
  "errors": null
}
```

### PATCH /orders/{id}/driver

**Request body:**
```json
{
  "driverId": "550e8400-e29b-41d4-a716-446655440000"
}
```

The `driverId` appears in the response only when assigned — omitted when null.

---

## Error responses

All errors follow a consistent format handled by `GlobalExceptionHandler`:

```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2026-05-13T19:37:19.263",
  "errors": [
    "customerName: Customer name is required"
  ]
}
```

| Status | Meaning |
|--------|---------|
| `400` | Validation error — check the `errors` field |
| `404` | Order not found |
| `422` | Invalid status transition |
| `500` | Unexpected server error |

---

## Running locally

**Prerequisites:** Docker, Java 21, Maven

**1. Start the infrastructure:**
```bash
docker-compose up -d
```

**2. Start Eureka Server** (from `eureka-server/`):
```bash
mvn spring-boot:run
```

**3. Start the service** (from `order-service/`):
```bash
mvn spring-boot:run
```

The service starts on port `8081`. Eureka dashboard: `http://localhost:8761`.

---

## Running tests

```bash
# from order-service/
mvn test
```

| Test class | Type | What it covers |
|---|---|---|
| `OrderTest` | Unit | Domain rules — factory methods, status transitions |
| `CreateOrderUseCaseImplTest` | Unit | Order creation use case with mocked repository |
| `UpdateOrderStatusUseCaseImplTest` | Unit | Status update, order not found, invalid transition |
| `OrderRepositoryAdapterTest` | Integration | JPA persistence with H2 in-memory database |

---

## Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Language |
| Spring Boot 3.2 | Application framework |
| Spring Data JPA | ORM and repository abstraction |
| PostgreSQL | Relational database (port `5433` via Docker) |
| Flyway | Database schema versioning |
| Lombok | Boilerplate reduction |
| Bean Validation | Input validation on DTOs |
| Spring Cloud Netflix Eureka | Service discovery |
| Spring Boot Actuator | Health checks and metrics |
| JUnit 5 + Mockito | Unit testing |
| H2 | In-memory database for integration tests |

---

## Patterns

| Pattern | Where |
|---------|-------|
| Clean Architecture | Package structure across all layers |
| Factory Method | `Order.create()` and `Order.reconstitute()` |
| Value Object | `Address` — no identity, defined by its values |
| Ports and Adapters | `OrderRepositoryPort` ↔ `OrderRepositoryAdapter` |
| Optimistic Locking | `@Version` on `OrderJpaEntity` |
| Status Transition Guard | `ALLOWED_TRANSITIONS` map in `Order.updateStatus()` |
| Global Exception Handler | `GlobalExceptionHandler` with `@RestControllerAdvice` |
| Schema Migration | Flyway versioned SQL files in `db/migration/` |