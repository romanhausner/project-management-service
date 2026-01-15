# Project Management Service

A backend-focused **Spring Boot REST API** for managing projects and tasks, designed with clean layering, explicit domain logic, and solid test coverage.

---

## Tech Stack

- Java 21
- Spring Boot 3.3.7
- Spring Web (REST), Spring Data JPA
- PostgreSQL
- Jackson
- Maven
- Docker / Docker Compose
- JUnit 5, Mockito, MockMvc

---

## Features

### Projects
- Full CRUD operations
- Partial updates via `PATCH`
- Project status lifecycle (`PLANNED`, `IN_PROGRESS`, `COMPLETED`)
- Validation and centralized exception handling

### Tasks
- Full CRUD operations
- Partial updates via `PATCH`
- Tasks belong to exactly one project
- Task status lifecycle (`TODO`, `IN_PROGRESS`, `DONE`)
- Priority, due date and assignee support
- Domain rules enforced in the service layer (e.g. immutable project assignment)

---

## API Overview

### Projects

```http
GET    /api/v1/projects
POST   /api/v1/projects
GET    /api/v1/projects/{id}
PUT    /api/v1/projects/{id}
PATCH  /api/v1/projects/{id}
DELETE /api/v1/projects/{id}
```

### Tasks

```http
GET    /api/v1/tasks
POST   /api/v1/tasks
GET    /api/v1/tasks/{id}
PUT    /api/v1/tasks/{id}
PATCH  /api/v1/tasks/{id}
DELETE /api/v1/tasks/{id}
```

## API Documentation (OpenAPI / Swagger)

The API is fully documented using **OpenAPI 3** and can be explored interactively via **Swagger UI**.

After starting the application, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI specification can be accessed at:

```
http://localhost:8080/v3/api-docs
```

The documentation includes:
- All available endpoints
- Request and response schemas
- HTTP status codes and error responses
- Example payloads


---

## Architecture

```text
Controller  → HTTP layer & validation
Service     → transactional business logic
Repository  → persistence (JPA)
Model       → domain entities & invariants
DTO / Mapper→ API contracts & mapping
```

---

## Testing

- Controller tests using `@WebMvcTest` and `MockMvc`
- Integration tests using `@SpringBootTest` with real repositories
- Mapper logic covered by unit tests

---

## Running Locally

### Database

Create a `.env` file:

```env
POSTGRES_USER=postgres
POSTGRES_PASSWORD=secret
POSTGRES_DB=project_management
POSTGRES_PORT=5432
```

Start PostgreSQL via Docker Compose:

```bash
docker-compose up -d
```

---

### Application

Set the environment variables before starting the application, for example via your IDE run configuration:

```env
DB_URL=jdbc:postgresql://localhost:5432/postgres
DB_USER=postgres
DB_PASSWORD=secret
```

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

Base URL:

```text
http://localhost:8080/api/v1
```

## License

This project is licensed under the MIT License.
