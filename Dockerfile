FROM gradle:8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/string-life-backend.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
