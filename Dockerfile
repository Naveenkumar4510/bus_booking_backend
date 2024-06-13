
# Use a base image with JDK and Maven installed
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package

# Use a smaller image for runtime
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
EXPOSE 9090
COPY --from=build /app/target/BusBooking-0.0.1-SNAPSHOT.jar /app/BusBooking.jar
CMD ["java", "-jar", "/app/BusBooking.jar"]
