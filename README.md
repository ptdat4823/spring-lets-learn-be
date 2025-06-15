# LetsLearn Backend

This is a Spring Boot application using PostgreSQL, built and run entirely with Docker.

---

## 🚀 Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop) installed and running on your system

---

## 🛠️ How to Run

1. Open your terminal (PowerShell, CMD, or Bash)

2. Navigate to the root folder of this project (where `docker-compose.yml` is located):

   ```bash
   cd path/to/letslearn-backend
   ```

3. Run the application using Docker Compose:

```bash
docker compose up
```

This will:

- Build the Spring Boot app using Maven

- Start a PostgreSQL container

- Run the backend on http://localhost:8080

## 📂 Project Overview

- spring-app (Dockerfile): Java 17 + Maven + Spring Boot

- db: PostgreSQL 15, running in Docker

- application.properties: All DB connection settings are hardcoded here, no dynamic env variables

## 🗃️ Database Info

The PostgreSQL service is preconfigured in application.properties as:

```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/lets-learn
spring.datasource.username=postgres
spring.datasource.password=root
```

**No local installation of PostgreSQL is required — Docker handles it for you.**
