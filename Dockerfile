# Use a base image with OpenJDK 17
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the application JAR file into the container
COPY --from=buld /target/BusBooking-0.0.1-SNAPSHOT.jar /app/BusBooking.jar


# Expose the port your application will run on
EXPOSE 9090


# Command to run the application
CMD ["java", "-jar", "BusBooking.jar"]
