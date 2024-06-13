# Use a base image with OpenJDK 17

FROM maven:3.8.4-openjdk-17 AS build
#WORKDIR /app
COPY . .
RUN mvn clean package
# Set the working directory in the container
#WORKDIR /app

FROM openjdk:17.0.1-jdk-slim
# Copy the application JAR file into the container
COPY --from=build /app/target/BusBooking-0.0.1-SNAPSHOT.jar /app/BusBooking.jar
# Expose the port your application will run on
EXPOSE 9090
# Command to run the application
CMD ["java", "-jar", "BusBooking.jar"]
