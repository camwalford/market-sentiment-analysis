# Stage 1: Build the application
ARG JAVA_VERSION
FROM eclipse-temurin:${JAVA_VERSION:-21}-jdk-alpine AS build
WORKDIR /app
COPY . .

# Change the line endings to Unix-style and ensure the gradlew script is executable
RUN sed -i 's/\r$//' gradlew
RUN chmod +x gradlew
RUN ls -la
RUN ./gradlew clean build --no-daemon

# Stage 2: Run the application
ARG JAVA_VERSION
ARG PORT
FROM eclipse-temurin:${JAVA_VERSION:-21}-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE ${PORT}

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
