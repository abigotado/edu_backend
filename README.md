# Education Platform Backend

Spring Boot 3.4 backend for an educational platform. The service manages courses, enrollments, assignments, quizzes, authentication, and exposes a REST API backed by PostgreSQL with Flyway migrations.

## Prerequisites

- Java 17+ (JDK 17 recommended)
- Gradle (wrapper included)
- Docker (required for Testcontainers-driven tests)
- PostgreSQL 14+ (or Dockerised instance)

## Project Structure

- `src/main/java`: Application source (domain entities, services, controllers, security)
- `src/main/resources`: Application configuration and Flyway migrations (`db/migration`)
- `src/test/java`: Integration tests (Spring Boot + Testcontainers) and focused unit tests
- `docs/`: Architecture notes, domain model, migration plan, and API planning documents

## Configuration

Configuration is managed via layered `application*.yml` files:

- `application.yml`: Base configuration with profile defaults, JPA/Flyway settings, and JWT properties
- `application-dev.yml`: Development datasource, Hikari pool settings, additional logging
- `application-test.yml`: Test profile using Testcontainers; Flyway clean enabled

Key environment variables (override defaults as needed):

| Variable | Purpose | Default |
| --- | --- | --- |
| `DEV_DB_URL` | JDBC URL for local Postgres | `jdbc:postgresql://localhost:5432/edu_backend_dev` |
| `DEV_DB_USERNAME` | Username for local Postgres | `edu_dev` |
| `DEV_DB_PASSWORD` | Password for local Postgres | `edu_dev` |
| `JWT_SECRET` | HMAC secret for JWT tokens | `your-secret-key-with-at-least-256-bits-for-HS256-algorithm` |

## Database Migrations

Flyway SQL migrations live under `src/main/resources/db/migration`:

1. `V1__baseline.sql` – initializes Flyway metadata
2. `V2__create_core_schema.sql` – full relational schema covering users, courses, lessons, assignments, quizzes, etc.
3. `V3__seed_demo_data.sql` – demo dataset for development/integration testing

Flyway runs automatically on application startup for each profile. Ensure your database user has rights to create/alter schema.

### Running Migrations Manually

```bash
./gradlew flywayMigrate -Dflyway.url=jdbc:postgresql://localhost:5432/edu_backend_dev \
  -Dflyway.user=edu_dev -Dflyway.password=edu_dev
```

## Running the Application

1. Ensure Postgres is running and accessible with configured credentials.
2. Export any overriding environment variables (if not using defaults).
3. Launch the Spring Boot application:

```bash
./gradlew bootRun
```

The API listens on `http://localhost:8080` by default. JWT authentication is required for most endpoints; use `/api/auth/register`, `/api/auth/login`, and `/api/auth/refresh` to obtain tokens.

## Testing

The project uses JUnit 5 with Testcontainers (Postgres) for integration coverage plus focused unit tests.

```bash
JAVA_HOME=$(usr/libexec/java_home -v 17) ./gradlew test
```

> Note: Docker must be running for Testcontainers. The test profile seeds demo data via Flyway.

### Selected Test Suites

- `EnrollmentControllerIntegrationTest` – exercises enrollment endpoints and security rules
- `QuizServiceIntegrationTest` – validates quiz attempt lifecycle and scoring
- `JwtTokenProviderTest` – unit-level token generation/validation checks

## API Overview

High-level REST endpoints (see controllers under `src/main/java/.../web/controller`):

- `/api/auth/**` – Authentication and token refresh
- `/api/courses/**` – Course discovery and management
- `/api/modules/**`, `/api/lessons/**` – Modular course content
- `/api/assignments/**`, `/api/submissions/**` – Assignment workflow
- `/api/quizzes/**` – Quiz creation and attempt submission
- `/api/enrollments/**` – Enrollment lifecycle for students and administrators

Public GET access is available for course/module/lesson discovery; other operations require roles (`STUDENT`, `TEACHER`, `ADMIN`).

## Docker & Deployment (Upcoming)

- Dockerfile and compose definitions are scheduled under `phase6-devops`
- CI pipeline integration is also part of the remaining roadmap

## Useful Commands

```bash
# Compile without running tests
./gradlew compileJava

# Run only a specific test class
./gradlew test --tests "com.masters.edu.backend.service.quiz.QuizServiceIntegrationTest"

# Build an executable jar
./gradlew bootJar
```

## Troubleshooting

- **Flyway validation errors** – ensure your database is empty/clean for the current migration baseline
- **Testcontainers initialization issues** – verify Docker daemon availability and network access to pull base images
- **JWT authentication failures** – check `JWT_SECRET` length (HS256 requires ≥32 bytes)

## Roadmap

- `phase6-devops`: containerization artifacts and optional CI workflow
- Further documentation: API contract details (OpenAPI/Swagger) and UI integration guide


