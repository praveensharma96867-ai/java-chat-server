# Step 1: Build the application using Maven and Java 26
FROM maven:3.9.6-eclipse-temurin-26 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the application using a lightweight Java 26 runtime
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/target/JavaInteractiveChat-1.0-SNAPSHOT.jar app.jar
EXPOSE 12345

# The flag below tells Java it does not need a visual screen monitor to run
CMD ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]