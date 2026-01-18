# ============================
# STAGE 1 : BUILD
# ============================
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copier uniquement le module config-server
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml pom.xml
COPY src src

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ============================
# STAGE 2 : RUNTIME
# ============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8888

HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8888/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
