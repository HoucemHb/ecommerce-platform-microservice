# ===========================================
# DOCKERFILE FOR CONFIG SERVER
# File: infrastructure/config-server/Dockerfile
# ===========================================

# ============================
# STAGE 1: BUILD
# ============================
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# ============================
# STAGE 2: RUNTIME
# ============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install wget for health checks
RUN apk add --no-cache wget

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create config directory
RUN mkdir -p /config

EXPOSE 8888

# Health check
HEALTHCHECK --interval=10s --timeout=3s --retries=5 --start-period=40s \
  CMD wget --quiet --tries=1 --spider http://localhost:8888/actuator/health || exit 1

# Run with native profile explicitly
ENTRYPOINT ["java", \
  "-Dspring.profiles.active=native,docker", \
  "-Dspring.cloud.config.server.native.search-locations=classpath:/config,file:/config", \
  "-jar", "app.jar"]