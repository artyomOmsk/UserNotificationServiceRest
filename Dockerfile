FROM maven:3.8.3-openjdk-17-slim AS build

WORKDIR /usr/src/app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn -DskipTests clean package

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /usr/src/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
