FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn package -Dquarkus.package.type=uber-jar

FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ ./
EXPOSE 8080
ENV PORT=8080
CMD ["java", "-jar", "target/ping-*-runner.jar"]