# Use the official OpenJDK 17 image as the base.
# We use the 'slim' variant to keep the image size as small as possible.
FROM registry.access.redhat.com/ubi9/openjdk-17-runtime

# Set the working directory inside the container.
WORKDIR /app

# Copy the built Spring Boot fat JAR file from your project's 'target' directory
# to the container's /app directory.
# You must first build your application to create this JAR.
# For example, using Maven: ./mvnw package
COPY target/mybooksapp-1.0.0-SNAPSHOT.jar books-application.jar

# Expose the port that the Spring Boot application runs on.
# The default for Spring Boot is port 8080.
EXPOSE 8080

# Define the command to run the application when the container starts.
# This command executes the JAR file using Java.
CMD ["java", "-jar", "books-application.jar"]