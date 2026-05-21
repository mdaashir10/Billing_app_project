# Dockerized Billing Application

A simple Java + MySQL billing application running in Docker containers.

## Prerequisites (to run locally)

- javac and mysql installed
- MySQL Connector JAR file in `lib/` folder

### Run locally

- Give your own password in "src/database/DatabaseConnection.java" at line 18
- bash/cmd
$ javac -cp "lib/mysql-connector-j-9.7.0.jar" -d bin src/database/*.java src/models/*.java src/dao/*.java src/ui/*.java
$ java -cp "bin;lib/mysql-connector-j-9.7.0.jar" ui.BillingApp

## Prerequisites (to run using Docker)

- Docker installed
- Docker Compose installed
- MySQL Connector JAR file in `lib/` folder

## Quick Start

### 1. Download MySQL Connector

Download MySQL Connector/J 8.0.33 from:
https://dev.mysql.com/downloads/connector/j/

Extract and place `mysql-connector-java-8.0.33.jar` in the `lib/` folder.

### 2. Build and Run

```bash
# Start both containers
docker-compose up --build

# Or run in detached mode
docker-compose up -d --build
```

### 3. Access the Application

The application runs in interactive mode. To access it:

```bash
# Attach to the running container
docker attach billing_java_app
```

### 4. Stop the Application

```bash
# Stop and remove containers
docker-compose down

# Stop and remove containers + volumes (deletes data)
docker-compose down -v
```

## Project Structure

```
BillingApp/
├── src/                    # Java source code
│   ├── database/
│   ├── models/
│   ├── dao/
│   └── ui/
├── lib/                    # MySQL connector JAR
├── sql/                    # Database initialization scripts
│   └── init.sql
├── Dockerfile              # Docker image definition
├── docker-compose.yml      # Multi-container orchestration
└── .dockerignore          # Docker ignore rules
```

## Docker Architecture

- **MySQL Container**: Runs MySQL 8.0 with persistent storage
- **Java App Container**: Runs the billing application
- **Network**: Both containers communicate via a bridge network
- **Volume**: MySQL data persists in a Docker volume

## Configuration

### Environment Variables (docker-compose.yml)

MySQL:
- `MYSQL_ROOT_PASSWORD`: rootpassword
- `MYSQL_DATABASE`: billing_app
- `MYSQL_USER`: billing_user
- `MYSQL_PASSWORD`: billing123

Java App:
- `DB_HOST`: mysql
- `DB_PORT`: 3306
- `DB_NAME`: billing_app
- `DB_USER`: billing_user
- `DB_PASSWORD`: billing123

### Ports

- MySQL: `3307:3306` (host:container)
  - Mapped to 3307 on host to avoid conflicts with local MySQL

## Common Commands

```bash
# View logs
docker-compose logs

# View logs for specific service
docker-compose logs mysql
docker-compose logs billing_app

# Restart services
docker-compose restart

# Execute MySQL commands
docker exec -it billing_mysql mysql -u billing_user -pbilling123 billing_app

# Check container status
docker-compose ps

# Rebuild without cache
docker-compose build --no-cache

# Scale (not applicable for this app but good to know)
docker-compose up --scale billing_app=1
```

## Accessing MySQL Directly

### From Host Machine

```bash
mysql -h 127.0.0.1 -P 3307 -u billing_user -pbilling123 billing_app
```

### From Docker Container

```bash
docker exec -it billing_mysql mysql -u billing_user -pbilling123 billing_app
```

## Troubleshooting

### Application can't connect to MySQL

```bash
# Check if MySQL is healthy
docker-compose ps

# View MySQL logs
docker-compose logs mysql

# Restart services
docker-compose restart
```

### Port conflicts

If port 3307 is already in use, modify `docker-compose.yml`:
```yaml
ports:
  - "3308:3306"  # Change to a different port
```

### Data persistence

To completely reset the database:
```bash
docker-compose down -v  # Removes volumes
docker-compose up --build
```

### MySQL connection timeout

The app includes a wait script that ensures MySQL is ready before starting.
If issues persist, increase the sleep time in the Dockerfile wait script.

## Features

✓ Fully containerized Java + MySQL application
✓ Persistent data storage
✓ Automatic database initialization
✓ Health checks for MySQL
✓ Interactive console interface
✓ Easy deployment and teardown
✓ Network isolation

## Development vs Production

This setup is suitable for:
- Development environments
- Testing
- College assignments
- Learning Docker

For production, consider:
- Using secrets management
- Implementing proper logging
- Adding monitoring
- Using production-grade MySQL configuration
- Implementing backup strategies

## License

Free to use for educational purposes.