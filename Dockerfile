FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/quarkus-app ./quarkus-app

EXPOSE 8080

CMD ["java", "-jar", "quarkus-app/quarkus-run.jar"]