# Spring Playground

A Spring Boot application for experimenting with authentication, authorization, and PostgreSQL-backed persistence.

## Requirements

- Java 25
- PostgreSQL

## Setup

1. Create a PostgreSQL database and user, or use an existing local instance.
2. Copy `.env.example` to `.env` and set the database credentials and application port.
3. Start the application:

   ```sh
   ./mvnw spring-boot:run
   ```

Flyway applies database migrations from `src/main/resources/db/migration` at startup. Keep `.env` local; do not commit credentials.

## Common commands

```sh
make help    # list available commands
make deps    # resolve dependencies
make build   # package without running tests
make test    # run tests
make run     # start the application
make clean   # remove Maven build output
```

## Source layout

```text
src/main/java/com/weex/spring_playground/
├── auth/                 # Authentication endpoints
├── common/
│   ├── api/              # Shared API response types
│   └── exception/        # Shared exceptions and HTTP error handling
├── config/               # Spring and security configuration
├── controller/           # General application endpoints
└── user/                 # User, role, permission, and persistence code

src/main/resources/
└── db/migration/         # Versioned Flyway SQL migrations
```

Tests belong under `src/test/java` using the same package structure as the code they cover.
