FROM openjdk:17-jdk-alpine
LABEL authors="rocky"

WORKDIR /app
COPY ./target/aichat-service-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "-Dspring.profiles.active=prd", "aichat-service-0.0.1-SNAPSHOT.jar"]