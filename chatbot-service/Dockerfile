# Build from the repository root: docker build -f chatbot-service/Dockerfile .
# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY chatbot-service/pom.xml chatbot-service/
RUN mvn -f chatbot-service/pom.xml dependency:go-offline -B
COPY chatbot-service/src chatbot-service/src
RUN mvn -f chatbot-service/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/chatbot-service/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
