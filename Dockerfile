# Stage 1: Build the application and run tests
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
# Copy the pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .
# Download dependencies (offline mode)
RUN mvn dependency:go-offline
# Copy the rest of your source code
COPY . .
# Run tests and build the jar
RUN mvn clean package

# Stage 2: Run the application
FROM openjdk:21-jdk-slim
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/target/tkproject-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
