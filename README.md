# Bank Java App

A **micro‑service banking demo** built with modern **Java 24** & **Spring Boot 3.4**. It showcases authentication, account transactions and ATM cash operations, wired together with Kafka and fronted by a Spring Cloud **API Gateway**.

---

## Technologies

- Java 24
- Spring Boot 3.4
- Spring Cloud Gateway
- Maven 4
- PostgreSQL 15
- Apache Kafka 3.9
- JPA (Hibernate)
- JUnit 5 & MockMvc
- Testcontainers

---

## Repository layout

```
api-gateway/         # Spring Cloud Gateway
auth-lib/            # shared auth utilities (DTOs, JWT helpers)
auth-service/        # user auth & registration
transaction-service/ # ledger & money transfers
atm-service/         # cash‑in / cash‑out
docker-compose.yml   # local stack
init-db.sql          # bootstrap script for Postgres
```

---

## Micro‑services

| Service                 | Container             | Port     | Gateway path                             | Purpose                 |
| ----------------------- | --------------------- | -------- | ---------------------------------------- | ----------------------- |
| **API Gateway**         | `api-gateway`         | **8080** | `/auth/**`, `/transaction/**`, `/atm/**` | Single entry point      |
| **Auth Service**        | `auth-service`        | 8081     | `/auth/**`                               | JWT auth & users        |
| **Transaction Service** | `transaction-service` | 8082     | `/transaction/**`                        | Accounts & transfers    |
| **ATM Service**         | `atm-service`         | 8083     | `/atm/**`                                | Cash deposit / withdraw |

Each micro‑service exposes its own OpenAPI spec at `<service-host>/swagger-ui.html`. When running through the gateway, visit e.g. `http://localhost:8080/auth/swagger-ui.html`.

---

## Quick start (Docker)

```bash
# build all images
docker compose build
# run the whole stack
docker compose up -d
# follow logs
docker compose logs -f
```

Services are reachable once health checks pass:

- Gateway: [http://localhost:8080/](http://localhost:8080/)
- Auth: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)
- Transactions: [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)
- ATM: [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health)
- Kafka broker: `localhost:9092`
- Postgres: `localhost:5432` (user `postgres` / password `P@ssw0rd`)

### Seeding demo users

```bash
# inside the running container
docker compose exec auth-service java -jar app.jar --seeder=user
```

or from sources:

```bash
mvn -pl auth-service spring-boot:run -Dspring-boot.run.arguments="--seeder=user"
```

---

## Running tests

```bash
mvn test
```

---

## Environment variables

| Variable                         | Default                                | Used by          | Note                |
| -------------------------------- | -------------------------------------- | ---------------- | ------------------- |
| `SPRING_DATASOURCE_URL`          | `jdbc:postgresql://postgres:5432/<db>` | all services     | injected by compose |
| `SPRING_DATASOURCE_USERNAME`     | `postgres`                             | all              |                     |
| `SPRING_DATASOURCE_PASSWORD`     | `P@ssw0rd`                             | all              |                     |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092`                           | transaction, atm |                     |
| `JWT_SECRET`                     | `supersecret`                          | auth-service     | change in prod      |
