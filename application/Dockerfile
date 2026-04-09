# This Dockerfile ONLY packages the pre-compiled JAR
# No compilation happens here - just packaging

FROM eclipse-temurin:11-jre-alpine

# Install curl for healthchecks
RUN apk add --no-cache curl

# Create app directory
WORKDIR /app

# Copy the pre-compiled JAR file from the build stage
# The JAR should already exist from Maven build
COPY target/simple-java-app-*.jar app.jar

# Create a non-root user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]