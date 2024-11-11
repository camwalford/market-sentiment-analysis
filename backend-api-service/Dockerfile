# Use an official JDK runtime for Java 21
FROM eclipse-temurin:21-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from Gradle build to the container
COPY build/libs/*.jar app.jar

# Expose the application port (change if different)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
