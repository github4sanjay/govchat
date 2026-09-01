FROM gradle:9.1-jdk21 AS build
WORKDIR /workspace
COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre
RUN useradd --system --uid 10001 appuser
WORKDIR /app
COPY --from=build /workspace/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
