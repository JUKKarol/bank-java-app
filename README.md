# Bank Java App

A **microservice banking** built with modern **Java 25** & **Spring Boot 4.0**. It showcases authentication, account transactions, credits and ATM cash operations, wired together with Kafka and fronted by a Spring Cloud **API Gateway**.

## 🏗️ Architecture Overview

This banking application follows a microservices architecture pattern with:
- **API Gateway** as the single entry point for all client requests
- **Auth Service** for user authentication and JWT token management
- **Transaction Service** for account management and money transfers
- **Credit Service** for cash deposits and withdrawals
- **ATM Service** for cash deposits and withdrawals
- **Kafka** for asynchronous inter-service communication
- **PostgreSQL** databases (one per service) for data persistence

### Service Communication Flow
```
Client → API Gateway → Auth Service (JWT validation)
                    ↓
                    → Transaction Service ← Kafka → ATM Service
                                              ↓
                                        Credit Service 
```

---

## 🛠️ Technologies

| Category | Technology | Version |
|----------|------------|---------|
| Language | Java | 25 |
| Framework | Spring Boot | 4.0 |
| API Gateway | Spring Cloud Gateway | Latest |
| Build Tool | Maven | 4 |
| Database | PostgreSQL | 15 |
| Message Broker | Apache Kafka | 3.9 |
| ORM | JPA (Hibernate) | Latest |
| Testing | JUnit 5 & MockMvc | Latest |
| Container | Docker & Docker Compose | Latest |

---

## 📁 Repository Structure

```
bank-java-app/
├── api-gateway/         # Spring Cloud Gateway for routing
├── auth-lib/            # Shared auth utilities (DTOs, JWT helpers)
├── exception-lib/       # Shared exceptions and global exception handler
├── auth-service/        # User authentication & registration
├── transaction-service/ # Account management & money transfers
├── atm-service/         # Cash deposit/withdrawal operations
├── credit-service/      # Credit operations
├── docker-compose.yml   # Local development stack
└── init-db.sql          # Database initialization script
```

---

## 🔌 Microservices

| Service | Container | Port | Gateway Path | Purpose |
|---------|-----------|------|--------------|---------|
| **API Gateway** | `api-gateway` | **8080** | `/auth/**`, `/transaction/**`, `/atm/**` | Single entry point, request routing |
| **Auth Service** | `auth-service` | 8081 | `/auth/**` | JWT authentication, user management |
| **Transaction Service** | `transaction-service` | 8082 | `/transaction/**` | Accounts, balance, transfers |
| **ATM Service** | `atm-service` | 8083 | `/atm/**` | Cash deposits/withdrawals |
| **Credit Service** | `credit-service` | 8084 | `/credit/**` | Credits |

Each microservice exposes its own OpenAPI documentation at `<service-host>/swagger-ui.html`. 

http://localhost:8081/swagger-ui/index.html

When running through the gateway:
- Auth API docs: `http://localhost:8080/auth/swagger-ui/index.html`
- Transaction API docs: `http://localhost:8080/transaction/swagger-ui/index.html`
- ATM API docs: `http://localhost:8080/atm/swagger-ui/index.html`
- Credit API docs: `http://localhost:8080/credit/swagger-ui/index.html`

---

## 🚀 Quick Start (Docker)

### Prerequisites
- Docker and Docker Compose installed
- At least 4GB of free RAM
- Ports 8080-8084, 5432, 9092, 2181 available

### Running the Application

```bash
# Clone the repository
git clone https://github.com/jukkarol/bank-java-app.git
cd bank-java-app

# Build all service images
docker compose build

# Start the entire stack
docker compose up -d

# Monitor logs
docker compose logs -f

# Stop all services
docker compose down
```

### Health Check Endpoints

Verify services are running:
- Gateway: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- Auth: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)
- Transaction: [http://localhost:8082/actuator/health](http://localhost:8082/actuator/health)
- ATM: [http://localhost:8083/actuator/health](http://localhost:8083/actuator/health)
- Credit: [http://localhost:8084/actuator/health](http://localhost:8084/actuator/health)

### Infrastructure Endpoints
- Kafka broker: `localhost:9092`
- Zookeeper: `localhost:2181`
- PostgreSQL: `localhost:5432` (user: `postgres`, password: `P@ssw0rd`)

---

## 👤 Demo Data Setup

### Seeding Demo Users

```bash
# Using Docker
docker compose exec auth-service java -jar app.jar --seeder=user

# From source code
mvn -pl auth-service spring-boot:run -Dspring-boot.run.arguments="--seeder=user"
```

This creates demo users with predefined credentials for testing purposes.

---

## 📋 API Endpoints

### Authentication Service
- `POST /auth/signup` - Register new user
- `POST /auth/login` - Login and receive JWT token
- `GET /auth/roles` - Get user roles
- `POST /auth/roles` - Create new role
- `DELETE /auth/roles` - Delete role

### Transaction Service
- `POST /transaction/accounts` - Create new account
- `GET /transaction/accounts` - Get user's accounts
- `GET /transaction/accounts/{accountId}` - Get account details
- `POST /transaction/transactions` - Make a transfer
- `GET /transaction/accounts/{accountId}/transactions` - Get account transactions

### ATM Service
- `POST /atm/deposits` - Make a cash deposit
- `POST /atm/withdrawals` - Make a cash withdrawal

### Credit Service
- `POST /credit/tools` - Process specified installments by credit id (test env only)
- `POST /credits` - Create credit for account number
- `GET /credits/{accountNumber}` - Get credits for account number
- `GET /credits/history/{creditId}` - Get credits history for credit

---

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Run Service-Specific Tests
```bash
# Auth service tests
mvn -pl auth-service test

# Transaction service tests
mvn -pl transaction-service test

# ATM service tests
mvn -pl atm-service test
```

### Test Coverage
The project includes:
- Unit tests for services and mappers
- Integration tests for controllers
- MockMvc tests for REST endpoints

---

## 💻 Development Setup

### Prerequisites
- JDK 25
- Maven 4
- Docker (for PostgreSQL and Kafka)

### Local Development

1. **Start infrastructure services only:**
```bash
docker compose up -d postgres kafka zookeeper
```

2. **Run services locally:**
```bash
# Terminal 1 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 2 - Transaction Service
cd transaction-service
mvn spring-boot:run

# Terminal 3 - ATM Service
cd atm-service
mvn spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway
mvn spring-boot:run
```

### Building from Source
```bash
# Build all modules
mvn clean install

# Build specific module
mvn -pl auth-service clean install
```

---

## 🔧 Configuration

### Environment Variables

| Variable | Default | Services | Description |
|----------|---------|----------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://postgres:5432/<db>` | All services | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | All services | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `P@ssw0rd` | All services | Database password |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | Transaction, ATM | Kafka broker address |
| `JWT_SECRET` | `supersecret` | Auth Service | JWT signing key (⚠️ change in production) |
| `SERVER_PORT` | Service-specific | All services | Service port |

### Application Properties
Each service has its own `application.yml` in `src/main/resources/` with specific configurations for:
- Database connections
- Kafka topics
- Logging levels
- JWT settings
- API documentation

---

## 🛡️ Security

### Authentication Flow
1. User registers/logs in via Auth Service
2. Auth Service returns JWT token
3. Client includes JWT in Authorization header for subsequent requests
4. API Gateway validates JWT before routing to services

### Security Best Practices
- Change default passwords in production
- Use strong JWT secrets
- Enable HTTPS in production
- Implement rate limiting
- Regular security updates

---

## 🐛 Troubleshooting

### Common Issues

**Services fail to start:**
- Ensure all required ports are free
- Check Docker daemon is running
- Verify PostgreSQL and Kafka are healthy

**Authentication errors:**
- Verify JWT_SECRET is consistent across services
- Check token expiration
- Ensure Authorization header format: `Bearer <token>`

**Kafka connection issues:**
- Wait for Kafka to fully initialize (60+ seconds)
- Check SPRING_KAFKA_BOOTSTRAP_SERVERS configuration
- Verify topic creation

### Useful Commands
```bash
# View all containers
docker compose ps

# Check service logs
docker compose logs -f [service-name]

# Restart a specific service
docker compose restart [service-name]

# Clean up everything
docker compose down -v
docker compose build --no-cache
```

---

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.