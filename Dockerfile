# Stage 1: Build the application
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /build/app

# Copy the parent POM and app module
COPY pom.xml ./
COPY src ./src/

# Build the app module
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /build/app/target/*.jar /acme-booking.app

# Define the entry point
ENTRYPOINT ["java", "-jar", "/acme-booking.app"]
