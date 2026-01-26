FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /build
COPY . .
RUN mvn clean package -pl notifications-service -am -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget
COPY --from=build /build/notifications-service/target/notifications-service-*.jar app.jar
EXPOSE 8083
HEALTHCHECK --interval=30s --timeout=3s --retries=3 --start-period=60s \
  CMD wget --quiet --tries=1 --spider http://localhost:8083/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx512m", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
