# Build from the repository root: docker build -f account-service/Dockerfile .
# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY account-service/pom.xml account-service/
RUN mvn -f account-service/pom.xml dependency:go-offline -B
COPY account-service/src account-service/src
RUN mvn -f account-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/account-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
