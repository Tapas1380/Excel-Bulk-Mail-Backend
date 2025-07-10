# -------- Stage 1: Build the application --------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Set working directory inside the container
WORKDIR /app

# Copy everything to the container
COPY . .

# Build the application without running tests (faster builds)
RUN ./mvnw clean package -DskipTests

# -------- Stage 2: Run the application --------
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /app/target/book-1.0-SNAPSHOT.war app.war


# Expose port 8080 (Spring Boot default)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.war"]
