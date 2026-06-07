FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml ./
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-alpine
WORKDIR /app
COPY --from=build /build/target/portal-backend-0.1.0.jar ./portal-backend.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "./portal-backend.jar"]
