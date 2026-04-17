#stage1: Build
FROM maven:3.9.14-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

#stage2:run 
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup
COPY --from=build /app/target/*.jar app.jar 

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]