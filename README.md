# Microservices Project

## Overview

`microServicesProject` is a Spring Boot microservices application organized as a single repository. Each service is maintained in its own directory and can be developed and built independently.

## Project Structure

```text
microServicesProject/
├── admin-service/
├── agent-service/
├── apigateway/
├── authservice/
├── customer-service/
├── eureka-server/
├── restaurant-service/
└── README.md
```

### Services

- **admin-service** — Admin-related functionality.
- **agent-service** — Agent-related functionality.
- **apigateway** — API gateway for routing requests to services.
- **authservice** — Authentication-related functionality.
- **customer-service** — Customer-related functionality.
- **eureka-server** — Service discovery using Netflix Eureka.
- **restaurant-service** — Restaurant-related functionality.

## Technologies

- Java
- Spring Boot
- Spring Cloud
- Maven
- Netflix Eureka
- Git and GitHub

*The exact Java and Spring Boot versions depend on each service's configuration.*

## Prerequisites

Before running the application, install:

- Java Development Kit (JDK) compatible with the services' Maven configuration.
- Git.
- A terminal or IDE, such as IntelliJ IDEA or Eclipse.

Maven can be invoked through the Maven Wrapper included with each service.

## Getting Started

### 1. Clone the repository

```bash
git clone <your-github-repository-url>
cd microServicesProject
```

Replace `<your-github-repository-url>` with your repository's GitHub URL.

### 2. Configure the services

Review the `application.properties.example` files in the relevant services.

Create a local `application.properties` file for each service as needed, and configure the required ports, service URLs, database settings, and credentials.

**Do not commit passwords, API keys, or other sensitive configuration values.**

### 3. Build a service

Navigate to the service directory and run the Maven Wrapper.

On Linux or macOS:

```bash
cd eureka-server
./mvnw clean package
```

On Windows:

```bat
cd eureka-server
mvnw.cmd clean package
```

Repeat for other services as needed.

### 4. Run the services

Start the services individually using your IDE or the Maven Wrapper.

For example, from the `eureka-server` directory:

```bash
./mvnw spring-boot:run
```

On Windows:

```bat
mvnw.cmd spring-boot:run
```

Start the remaining services according to their configuration and dependencies. Ensure that any required databases and external services are running.

## Configuration

Service-specific configuration is located under:

```text
<service-name>/src/main/resources/
```

Check each service's configuration before running it. Ports, database requirements, and service URLs may differ between services.

## Development

Each service has its own `pom.xml` and Maven Wrapper, allowing it to be built separately.

Generated build output, such as Maven's `target/` directories, should not be committed to Git.