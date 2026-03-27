# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HS iPhones API — specialized ERP/PDV system for iPhone retail. Manages sales, BuyBack (refurbishment), internal repairs, and external service orders. Built with Spring Boot 4.0.2, Java 25, PostgreSQL, Flyway migrations.

## Build & Run Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run locally (requires PostgreSQL via docker-compose)
docker compose up -d
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=HsiphonesapiApplicationTests

# Compile only (fast check)
./mvnw compile
```

## Database

- PostgreSQL 15 via `docker-compose.yml` (localhost:5432, db: `hs_iphones`, user/pass: `postgres/postgres`)
- Flyway migrations in `src/main/resources/db/migration/` — naming: `V{timestamp}__{description}.sql`
- JPA ddl-auto is `validate` — all schema changes must go through Flyway migrations

## Architecture

Layered: **Controller → Service (interface + impl) → Repository → JPA Entity**

- `controller/` — REST endpoints with Swagger annotations (`@Tag`, `@Operation`)
- `service/` — interfaces; `service/impl/` — implementations with `@Transactional`
- `repository/` — Spring Data JPA repositories with custom `@Query` methods
- `model/` — JPA entities; `model/enums/` — status/category enums
- `dto/request/` and `dto/response/` — request/response DTOs with Jakarta validation
- `mapper/` — manual entity ↔ DTO mappers (no MapStruct)
- `client/` — OpenFeign clients for external CEP APIs (ViaCEP → BrasilAPI → OpenCEP fallback chain)
- `validation/` — custom validators (e.g., `@CpfOrCnpj`)

## Key Business Domains

- **Products**: Categories (CELULAR, PECA, ACESSORIO), IMEI tracking via `@ElementCollection`, auto-generated SKU, status state machine (DISPONIVEL → VENDIDO/RESERVADO/IN_REPAIR/etc.)
- **Sales (PDV)**: State machine (ORCAMENTO → RESERVADO → CONCLUIDO), split payments, BuyBack-as-payment, HTML receipt via Thymeleaf (`templates/receipt.html`)
- **BuyBack**: Products originating from clients, tracked via `ProductRepair` with part consumption and labor costs
- **Service Orders**: External repair workflow with 9-state lifecycle (RECEIVED → DELIVERED/CANCELED), parts consumption tracking

## Language & Conventions

- All validation messages and business terms are in **Brazilian Portuguese**
- Lombok used extensively (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- WAR packaging (includes `ServletInitializer` for Tomcat deployment)
- Swagger UI at `/swagger-ui.html` when running
- Base package: `com.br.hsiphonesapi`
