FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk update && apk upgrade --no-cache

COPY --from=build /app/target/*.jar conecta-arena.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xmx400m", "-jar", "conecta-arena.jar"]
