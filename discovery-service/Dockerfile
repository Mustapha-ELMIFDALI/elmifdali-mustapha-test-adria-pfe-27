# Build from the repository root: docker build -f discovery-service/Dockerfile .
# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY discovery-service/pom.xml discovery-service/
RUN mvn -f discovery-service/pom.xml dependency:go-offline -B
COPY discovery-service/src discovery-service/src
RUN mvn -f discovery-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/discovery-service/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
