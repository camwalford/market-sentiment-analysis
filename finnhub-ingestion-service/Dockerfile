ARG JAVA_VERSION=21
FROM eclipse-temurin:${JAVA_VERSION}-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/*.jar app.jar

# Expose the application's port (if necessary)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
