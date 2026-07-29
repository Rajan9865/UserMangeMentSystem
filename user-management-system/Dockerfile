# =============================================================
# Stage 1 : BUILD
# Maven + JDK 21 to compile and package the application
# =============================================================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

LABEL stage="builder"

WORKDIR /app

# Copy pom.xml first — leverages Docker layer cache.
# Dependencies are re-downloaded only when pom.xml changes.
COPY pom.xml .

RUN mvn dependency:go-offline -B --no-transfer-progress

# Copy source code and build the JAR (skip tests for Docker build)
COPY src ./src

RUN mvn clean package -DskipTests -B --no-transfer-progress

# =============================================================
# Stage 2 : RUNTIME
# Lightweight JRE-only image — no Maven, no source code
# Final image size: ~180–250 MB instead of ~900 MB
# =============================================================
FROM eclipse-temurin:21-jre AS runtime

LABEL maintainer="user-management-system"
LABEL version="1.0"
LABEL description="User Management System - Spring Boot REST API"

WORKDIR /app

# Create a non-root user for security (enterprise best practice)
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Health check using Spring Boot Actuator
# Checks every 30s, 10s timeout, 3 retries, 40s start period for JVM warmup
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Production JVM options:
# -XX:+UseContainerSupport      → JVM respects container CPU/Memory limits
# -XX:MaxRAMPercentage=75.0     → Use 75% of container memory for heap
# -Djava.security.egd           → Faster startup by using /dev/urandom
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar"]
