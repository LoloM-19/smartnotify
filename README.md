# SmartNotify — Rate-Limited Notification Microservice

A production-grade notification microservice built with Java Spring Boot, demonstrating containerisation, async processing, rate limiting, and API documentation.

## Architecture
- **Spring Boot 4.0.8** — REST API
- **PostgreSQL** — Persistent notification storage
- **Redis** — Rate limiting (sliding window counter)
- **Docker Compose** — Container orchestration
- **Swagger/OpenAPI** — Interactive API docs

## Features
- Send EMAIL, SMS, and IN_APP notifications via REST API
- Async notification processing with `@Async`
- Rate limiting — max 5 requests per recipient per 60 seconds
- Structured logging with SLF4J
- Input validation with Jakarta Bean Validation
- Global exception handling
- Health monitoring via Spring Boot Actuator
- 6 unit tests with JUnit 5 and Mockito

## Quality Signals
- Containerised with Docker Compose
- Automated unit tests — `BUILD SUCCESS`
- Swagger/OpenAPI docs at `/swagger-ui/index.html`
- Structured JSON API responses
- Production-ready error handling

## How to Run

### Prerequisites
- Java 21
- Maven
- Docker & Docker Compose

### Steps
1. Clone the repository:
```bash
   git clone https://github.com/LoloM-19/smartnotify.git
   cd smartnotify
```
2. Start PostgreSQL and Redis:
```bash
   docker compose up postgres redis -d
```
3. Run the application:
```bash
   ./mvnw spring-boot:run
```
4. Open API docs at: `http://localhost:8080/swagger-ui/index.html`
5. Check health at: `http://localhost:8080/actuator/health`

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/notifications` | Send a notification |
| GET | `/api/v1/notifications` | Get all notifications |
| GET | `/api/v1/notifications/{id}` | Get notification by ID |
| GET | `/api/v1/notifications/recipient/{email}` | Get by recipient |
| GET | `/api/v1/notifications/rate-limit/{email}` | Check remaining requests |

## Running Tests
```bash
./mvnw test
```

## Tech Stack
Java | Spring Boot | PostgreSQL | Redis | Docker | JUnit 5 | Mockito | Swagger