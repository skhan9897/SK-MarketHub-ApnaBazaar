# Stage 1: Build React Web SPA
FROM node:20-alpine AS web-builder
WORKDIR /app/web
COPY web/package*.json ./
RUN npm ci
COPY web/ ./
RUN npm run build

# Stage 2: Build Spring Boot Backend with embedded React Web static assets
FROM eclipse-temurin:17-jdk-alpine AS backend-builder
WORKDIR /app
COPY backend/mvnw backend/
COPY backend/.mvn backend/.mvn
COPY backend/pom.xml backend/
COPY backend/src backend/src
COPY --from=web-builder /app/web/dist backend/src/main/resources/static/
WORKDIR /app/backend
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 3: Unified Production JRE Container
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/backend/target/backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
