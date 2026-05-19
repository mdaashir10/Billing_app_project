# Use OpenJDK 17 as base image
FROM openjdk:17-slim

# Set working directory
WORKDIR /app

# Install netcat for database wait script
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy MySQL connector library
COPY lib/mysql-connector-java-8.0.33.jar /app/lib/

# Copy source code
COPY src/ /app/src/

# Compile Java application
RUN javac -cp "/app/lib/mysql-connector-java-8.0.33.jar" \
    -d /app/bin \
    /app/src/database/*.java \
    /app/src/models/*.java \
    /app/src/dao/*.java \
    /app/src/ui/*.java

# Create wait-for script
RUN echo '#!/bin/sh\n\
echo "Waiting for MySQL to be ready..."\n\
while ! nc -z mysql 3306; do\n\
  sleep 1\n\
done\n\
echo "MySQL is ready!"\n\
sleep 5\n\
exec "$@"' > /app/wait-for-mysql.sh && chmod +x /app/wait-for-mysql.sh

# Set classpath and run application
CMD ["/app/wait-for-mysql.sh", "java", "-cp", "/app/bin:/app/lib/mysql-connector-java-8.0.33.jar", "ui.BillingApp"]
