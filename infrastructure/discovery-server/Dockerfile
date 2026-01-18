FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/discovery-server-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8761
HEALTHCHECK --interval=10s --timeout=3s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8761/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]