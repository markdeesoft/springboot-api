# 🚀 Enterprise Spring Boot 3 Clean Architecture REST API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Security](https://img.shields.io/badge/Spring%20Security-JWT-blue.svg)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A production-ready RESTful API backend prototype built with **Spring Boot 3.3.4** and **Java 21**, demonstrating **Clean Architecture**, **SOLID Principles**, and comprehensive **Unit & Integration Testing (JUnit 5, Mockito, MockMvc)**.

Designed specifically as a software engineering portfolio piece showcasing enterprise-grade backend development capabilities.

---

## 📌 Features

- **Authentication & Authorization**: Stateless JWT-based authentication integrated with Spring Security & Role-Based Access Control (RBAC).
- **User Profile Management**: Complete CRUD capabilities for managing user credentials, profiles, and password resets.
- **Unified Standard API Response**: Standardized JSON responses wrapping payload data (`$.data`) and meta status for client predictability.
- **Robust Exception Handling**: Global Controller Advice (`@RestControllerAdvice`) handling business validation and runtime exceptions with clean error payloads.
- **Database Resilience**: JPA/Hibernate ORM with PostgreSQL and automated schema migration via Flyway/Liquibase.

---

## 🏗️ Architecture & Project Structure

The project strictly follows **Clean Architecture** and **Layered Architecture** principles to separate concerns and maximize testability.

```text
src/main/java/com/example/app
├── config/            # Spring Security, JWT, CORS, and Bean Configurations
├── controller/        # REST Controllers (API Endpoints & Request Handling)
├── dto/               # Data Transfer Objects (Requests, Responses, API Wrappers)
├── exception/         # Custom Exceptions & Global Exception Handlers
├── model/             # Domain Entities (JPA Annotations, Database Schema)
├── repository/        # Data Access Layer (Spring Data JPA Repositories)
└── service/           # Business Logic Layer (Interfaces & Implementations)
```

---

## 🛠️ Tech Stack

- **JDK**: Java 21 (Virtual Threads ready)
- **Framework**: Spring Boot 3.3.4
- **Security**: Spring Security, JWT (JSON Web Tokens)
- **Persistence**: Spring Data JPA, Hibernate, PostgreSQL, H2 (Testing)
- **Boilerplate Reduction**: Lombok
- **Testing**: JUnit 5, Mockito, Spring Security Test (`MockMvc`, `@WithMockUser`), AssertJ
- **Build Tool**: Maven (`./mvnw`)

---

## 🧪 Testing Strategy & Quality Assurance

This project emphasizes test coverage across all core architecture layers using **Slice Testing** and **Unit Testing**:

- **Repository Layer (`@DataJpaTest`)**: Verifies custom JPQL queries and database mapping against an in-memory database.
- **Service Layer (`Mockito`)**: Tests pure business logic, input validation, and mapping contracts independently of the database framework.
- **Controller Layer (`@WebMvcTest` / `@SpringBootTest` + `MockMvc`)**: Validates HTTP status codes (`200 OK`, `201 Created`, `401 Unauthorized`, `403 Forbidden`), request body validation, and Security Filter Chains via `@WithMockUser` and `csrf()`.

### Running Tests

Execute the full test suite using Maven Wrapper:

```bash
# Run all tests in the project
./mvnw test

# Run a specific test class
./mvnw test -Dtest=ProfileControllerTest

# Run a single test method
./mvnw test -Dtest=UserServiceImplTest#updateUser_Success_WithDefaultRole
```

---

## 🔌 API Endpoints (Overview)

### Authentication & Public
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Register a new user | Public |
| `POST` | `/api/v1/auth/login` | Authenticate user & get JWT token | Public |

### User Profile Management
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/profile` | Get current authenticated user profile | Authenticated (`USER`, `ADMIN`) |
| `PUT` | `/api/v1/profile` | Update current user details | Authenticated (`USER`, `ADMIN`) |
| `PATCH` | `/api/v1/profile/resetpass` | Reset user password | Authenticated (`USER`, `ADMIN`) |

### Admin Operations
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/users` | List all registered users (supports pagination/sorting) | Admin (`ADMIN`) |
| `POST` | `/api/v1/users` | Create a new user with specific role | Admin (`ADMIN`) |
| `GET` | `/api/v1/users/{id}` | Get user details by ID | Admin (`ADMIN`) |
| `PUT` | `/api/v1/users/{id}` | Update existing user details by ID | Admin (`ADMIN`) |
| `DELETE` | `/api/v1/users/{id}` | Delete user account by ID | Admin (`ADMIN`) |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** installed
- **Docker** & **Docker Compose** (Optional, for PostgreSQL)

### Installation & Local Setup

1. **Clone the repository**
   ```bash
   git clone [https://github.com/markdeesoft/springboot-api.git](https://github.com/markdeesoft/springboot-api.git)
   cd your-repo-name
   ```

2. **Configure Database Settings**
   Update `src/main/resources/application.yml` or set environment variables for Database connection.

3. **Build & Run Application**
   ```bash
   ./mvnw clean spring-boot:run
   ```
   The application will start on `http://localhost:8080`.

---

## 📝 Contact & Developer Info

Developed by **Full Stack / Backend Software Engineer**

- **Portfolio/Website**: [http://deesoftware.com/profile](http://deesoftware.com/profile)
- **LinkedIn**: [https://www.linkedin.com/in/nathapan-kuntee](https://www.linkedin.com/in/nathapan-kuntee)
- **Email**: dev.deesoft@gmail.com