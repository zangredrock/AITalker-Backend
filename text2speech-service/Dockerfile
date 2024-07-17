FROM openjdk:17-jdk-alpine
LABEL authors="rocky"

WORKDIR /app
COPY ./target/text2speech-service-0.0.1-SNAPSHOT.jar /app
CMD ["java", "-jar", "-Dspring.profiles.active=prd", "text2speech-service-0.0.1-SNAPSHOT.jar"]