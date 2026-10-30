# Build from the repository root: docker build -f config-service/Dockerfile .
# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY config-service/pom.xml config-service/
RUN mvn -f config-service/pom.xml dependency:go-offline -B
COPY config-service/src config-service/src
RUN mvn -f config-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/config-service/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
