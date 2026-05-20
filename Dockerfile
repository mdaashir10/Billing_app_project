# Use Eclipse Temurin JDK 17 (official OpenJDK builds)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Install netcat and curl for database wait script and downloading MySQL connector
# Also install X11 libraries for Swing GUI
RUN apk add --no-cache netcat-openbsd curl xvfb font-dejavu

# Set display for GUI (using virtual frame buffer)
ENV DISPLAY=:99

# Download MySQL Connector JAR if not present in build context
RUN mkdir -p /app/lib && \
    curl -L -o /app/lib/mysql-connector-java-9.7.0.jar \
    https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-9.7.0.jar

# Copy MySQL connector library from build context (if it exists, it will override the downloaded one)
COPY lib/mysql-connector-java-8.0.33.jar /app/lib/ 2>/dev/null || true

# Copy source code
COPY src/ /app/src/

# Compile Java application
RUN javac -cp "/app/lib/mysql-connector-java-8.0.33.jar" \
    -d /app/bin \
    /app/src/database/*.java \
    /app/src/models/*.java \
    /app/src/dao/*.java \
    /app/src/ui/*.java

# Create wait-for and start script
RUN echo '#!/bin/sh\n\
echo "Waiting for MySQL to be ready..."\n\
while ! nc -z mysql 3306; do\n\
  sleep 1\n\
done\n\
echo "MySQL is ready!"\n\
sleep 5\n\
echo "Starting Xvfb for GUI..."\n\
Xvfb :99 -screen 0 1024x768x24 &\n\
sleep 2\n\
echo "Starting Billing Application GUI..."\n\
exec "$@"' > /app/wait-for-mysql.sh && chmod +x /app/wait-for-mysql.sh

# Expose port for VNC if needed (optional)
EXPOSE 5900

# Set classpath and run application
CMD ["/app/wait-for-mysql.sh", "java", "-cp", "/app/bin:/app/lib/mysql-connector-java-9.7.0.jar", "ui.BillingApp"]
