# Banking System – Microservices Architecture

A Spring Boot-based Banking Management System built using a **Microservices Architecture**. The application provides RESTful APIs for customer management, account management, and financial transactions, with centralized API routing, JWT-based security, and service discovery.

The project demonstrates the design and implementation of independent, scalable backend services using Java and the Spring ecosystem.

---

## Project Overview

The Banking System is divided into independently deployable microservices, each responsible for a specific business domain.

The application supports customer registration and management, bank account lifecycle operations, deposits, withdrawals, and balance updates through REST APIs.

### Key Features

- Customer registration and management
- Bank account creation and management
- Account balance management
- Deposit and withdrawal operations
- Transaction management
- RESTful API development
- JWT-based authentication and security
- Centralized API routing through API Gateway
- Service registration and discovery using Eureka
- API documentation using OpenAPI/Swagger
- Request validation and exception handling

---

## System Architecture

The application consists of five independent services.

| Microservice | Responsibility |
|---|---|
| Customer Management | Handles customer registration and customer-related operations |
| Account Management | Manages bank accounts and account-related operations |
| Transaction Management | Handles deposits, withdrawals, and financial transactions |
| Gateway Server | Provides centralized API routing and security |
| Eureka Server | Handles service registration and discovery |

### Architecture Flow

```text
                  Client / Frontend
                         |
                         |
                  API Gateway
                  (JWT Security)
                         |
             +-----------+-----------+
             |           |           |
             v           v           v
        Customer      Account    Transaction
        Service       Service      Service
             |           |           |
             +-----------+-----------+
                         |
                  Eureka Server
                (Service Discovery)
```

*Note: The diagram represents the logical service architecture. Actual service-to-service communication and database interactions depend on the configured implementation.*

---

## Technology Stack

| Category | Technologies |
|---|---|
| Programming Language | Java |
| Backend Framework | Spring Boot |
| Microservices | Spring Cloud |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Security | Spring Security, JWT |
| API Development | RESTful APIs |
| API Documentation | OpenAPI / Swagger |
| Build Tool | Maven |
| Version Control | Git, GitHub |

---

## Project Structure

```text
Banking_System/
│
├── customersmanagement/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
│
├── accountsmanagement/
│   ├── src/
│   └── pom.xml
│
├── transactionmanagement/
│   ├── src/
│   └── pom.xml
│
├── gatewayserver/
│   ├── src/
│   ├── API_DOCUMENTATION.md
│   ├── JWT_SECURITY_README.md
│   └── pom.xml
│
├── EurekaServer/
│   ├── src/
│   └── pom.xml
│
├── OPENAPI_DOCUMENTATION.md
├── TESTING_GUIDE.md
├── README.md
└── .gitignore
```

Each microservice follows a layered architecture to separate responsibilities and improve maintainability.

### Layered Architecture

- **Controller Layer:** Handles incoming HTTP requests and API responses.
- **Service Layer:** Implements business logic and application workflows.
- **Repository Layer:** Handles data access operations.
- **Entity Layer:** Defines the application's data models.
- **DTO Layer:** Manages request and response objects.
- **Exception Layer:** Handles application exceptions and error responses.
- **Config Layer:** Contains application and security configurations.

---

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- Java Development Kit (JDK)
- Maven
- Git
- An IDE such as IntelliJ IDEA or Eclipse
- Database configured as specified in the application properties

### 1. Clone the Repository

```bash
git clone https://github.com/BethuRohitash/Banking_System.git
```

### 2. Navigate to the Project

```bash
cd Banking_System
```

### 3. Configure Application Properties

Before starting the services, configure the required application properties, including:

- Server ports
- Database connection details
- Eureka server URL
- JWT/security configuration
- Service communication settings

Do not commit passwords, private keys, JWT secrets, or other sensitive credentials to GitHub.

### 4. Build the Microservices

Navigate into each microservice directory and execute:

```bash
./mvnw clean install
```

On Windows:

```bash
mvnw.cmd clean install
```

If Maven Wrapper is unavailable, use:

```bash
mvn clean install
```

### 5. Start the Microservices

Start the services in the following order:

1. Eureka Server
2. Customer Management Service
3. Account Management Service
4. Transaction Management Service
5. Gateway Server

Run each service from its respective directory:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Use the configured service ports and Eureka URL when starting the application.

---

## API Documentation

The application provides API documentation using OpenAPI/Swagger.

Refer to the following documentation files:

- `OPENAPI_DOCUMENTATION.md`
- `gatewayserver/API_DOCUMENTATION.md`
- `gatewayserver/JWT_SECURITY_README.md`

These documents provide details about available API endpoints, security configurations, and API usage.

---

## Testing

The project includes testing documentation and test source directories.

Refer to:

`TESTING_GUIDE.md`

To execute tests for an individual microservice:

```bash
./mvnw test
```

---

## Security

The Gateway Server is responsible for centralized API routing and JWT-based security.

The security implementation is designed to authenticate incoming requests and control access to protected APIs.

Sensitive credentials and signing secrets should be configured externally rather than hardcoded in source code.

---

## Project Objectives

This project was developed to demonstrate practical knowledge of:

- Java and Spring Boot application development
- Microservices architecture and service decomposition
- REST API design and implementation
- Spring Cloud Gateway
- Service discovery using Eureka
- JWT-based authentication
- Layered backend architecture
- API documentation and testing
- Maven-based project management

---

## Maintainer

**Rohithash Bethu**

GitHub: [@BethuRohitash](https://github.com/BethuRohithash)

---

⭐ If you find this project useful, feel free to explore the repository and its microservices.
