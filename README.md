# fuzentrix-backend

This is the backend for the Fuzentrix EdTech platform, built with **Spring Boot 3**, **PostgreSQL**, and **MinIO** for object storage. It relies on **Flyway** for automated database schema migrations.

---

## 🛠 Prerequisites

Before starting, ensure you have the following installed on your machine:
- **Java 21** or higher
- **Maven**
- **Docker** and **Docker Compose**
- *(Optional)* Database client like DBeaver or pgAdmin (for easily inspecting data)

---

## 🚀 Local Setup Instructions

### 1. Start Infrastructure Dependencies
The local environment utilizes Docker Compose to easily spin up dependencies without having to manually install PostgreSQL and MinIO on your machine.

Run the following command from the root of the project:
```bash
docker-compose up -d
```
*(If you run into previous volume caching issues, run `docker-compose down -v` to clear old Postgres data, then `up -d` again).*

This will start:
- **PostgreSQL 15** on port `5432`
- **MinIO Server** on port `9000` (API) and `9001` (Console)

### 2. Run the Spring Boot Application
Once the infrastructure is up, simply start the Spring application:

```bash
mvn spring-boot:run
```

> **Note on Database Migrations (Flyway):** You do NOT need to create tables manually. When the Spring Boot application boots up for the first time, Flyway will automatically execute the schema migrations.

---

## 🧭 Navigating the Local Environment

After everything is running, here is how you can interact with the different pieces of the architecture:

### 🐘 PostgreSQL Database
- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `fuzentrix_db`
- **Username**: `fuzentrix`
- **Password**: `fuzentrix@pass`
> Connect with DBeaver, pgAdmin, or 
run `docker exec -it fuzentrix-postgres psql -U fuzentrix -d fuzentrix_db`

### 🪣 MinIO (Object Storage)
The system is configured to emulate AWS S3 storage under a single local bucket (`fuzentrix`). Paths and directories inside the bucket are handled virtually by the `MinioService.java`.
- **MinIO Web Console UI**: Open [http://localhost:9001](http://localhost:9001) in your browser.
- **Access Key / Username**: `minio`
- **Secret Key / Password**: `minio123`

### 🔌 API Backend
The application runs locally on port 8080.
- **Base URL**: `http://localhost:8080`
