# Build from the repository root: docker build -f transaction-service/Dockerfile .
# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY transaction-service/pom.xml transaction-service/
RUN mvn -f transaction-service/pom.xml dependency:go-offline -B
COPY transaction-service/src transaction-service/src
RUN mvn -f transaction-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/transaction-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
