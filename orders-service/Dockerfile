FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /build

# Copy everything from the build context (project root)
COPY . .

# Build only orders-service and its dependencies
RUN mvn clean package -pl orders-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget
COPY --from=build /build/orders-service/target/orders-service-*.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=60s \
  CMD wget --quiet --tries=1 --spider http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx512m", "-Dspring.profiles.active=docker", "-jar", "app.jar"]