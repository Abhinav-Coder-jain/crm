# Use a base image with Java 21 (as you specified)
FROM eclipse-temurin:21-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven build (JAR file) into the container
# Cloud Build will implicitly run 'mvn clean install' in a previous step
# and ensure target/*.jar exists before this COPY step.
COPY target/*.jar app.jar

# Cloud Run defaults to forwarding requests to port 8080.
# EXPOSE indicates what port the container intends to listen on.
EXPOSE 8080 # CRITICAL: This must match the port your Spring Boot app listens on

# Define the entrypoint command for the container
# This sets the Java app to listen on port 8080, matching Cloud Run's default and EXPOSE
ENTRYPOINT ["java", "-Dserver.port=8080", "-jar", "app.jar"]