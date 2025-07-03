FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package -Dquarkus.package.type=uber-jar -DskipTests

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ .
CMD ["java", "-jar", "quarkus-run.jar"]
