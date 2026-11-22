# Stage 1: Build Spring Boot JAR
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY backend/mvnw backend/
COPY backend/.mvn backend/.mvn
COPY backend/pom.xml backend/
COPY backend/src backend/src
WORKDIR /app/backend
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2: Production JRE Runtime Container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/backend/target/backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
