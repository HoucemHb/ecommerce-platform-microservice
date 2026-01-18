FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget
COPY --from=build /app/target/payments-service-*.jar app.jar
EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=60s \
  CMD wget --quiet --tries=1 --spider http://localhost:8083/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx512m", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
