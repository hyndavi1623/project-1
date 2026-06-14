# Build stage
FROM maven:3.9.8-eclipse-temurin-17 AS build

WORKDIR /javaproject

# Copy Maven configuration
COPY pom.xml .

# Download dependencies first (improves build caching)
RUN mvn dependency:go-offline

# Copy source code
COPY app.java .

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy generated JAR
COPY --from=build /app/target/*.jar app.jar

# Application port
EXPOSE 8080

# Start Spring Boot application
ENTRYPOINT ["java","-jar","app.jar"]
