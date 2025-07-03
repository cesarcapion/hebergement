FROM eclipse-temurin:21 AS build
WORKDIR /app
COPY . .
RUN mvn package -Dquarkus.package.type=uber-jar

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ .
CMD ["java", "-jar", "target/ping-*-runner.jar"]
