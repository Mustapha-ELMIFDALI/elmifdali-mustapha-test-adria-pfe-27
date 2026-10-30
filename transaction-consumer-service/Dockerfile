# Build from the repository root: docker build -f transaction-consumer-service/Dockerfile .
# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY transaction-consumer-service/pom.xml transaction-consumer-service/
RUN mvn -f transaction-consumer-service/pom.xml dependency:go-offline -B
COPY transaction-consumer-service/src transaction-consumer-service/src
RUN mvn -f transaction-consumer-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/transaction-consumer-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
