FROM eclipse-temurin:21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw package -Dquarkus.package.type=uber-jar

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /app/target/quarkus-app/ .
CMD ["java", "-jar", "quarkus-run.jar"]
