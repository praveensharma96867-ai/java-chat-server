# Step 1: Build the application using Maven and standard Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the application using a lightweight Java 21 runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/JavaInteractiveChat-1.0-SNAPSHOT.jar app.jar
EXPOSE 12345

# Explicitly running headless mode so it doesn't look for a screen monitor
CMD ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]