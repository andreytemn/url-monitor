# Build stage
FROM maven:3.8.3-openjdk-17 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

# Run stage
FROM openjdk:17-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
ARG JAR_FILE=/usr/src/app/target/url-monitor-1.0.0.jar
COPY --from=build ${JAR_FILE} url-monitor-1.0.0.jar
ENTRYPOINT ["java","-jar","url-monitor-1.0.0.jar"]