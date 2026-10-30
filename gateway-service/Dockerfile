# Build from the repository root: docker build -f gateway-service/Dockerfile .
# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY gateway-service/pom.xml gateway-service/
RUN mvn -f gateway-service/pom.xml dependency:go-offline -B
COPY gateway-service/src gateway-service/src
RUN mvn -f gateway-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/gateway-service/target/*.jar app.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "app.jar"]
