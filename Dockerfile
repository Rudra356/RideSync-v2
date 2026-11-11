#Building React Frontend
FROM node:22-alpine AS frontend-build

WORKDIR /app/frontend

COPY ridesync-frontend/package*.json ./
RUN npm ci

COPY ridesync-frontend/ ./
RUN npm run build

#Building Spring Boot Backend

FROM maven:3.9.12-eclipse-temurin-21 AS backend-build

WORKDIR /app

COPY ridesync/pom.xml ./ridesync/pom.xml
COPY ridesync/src ./ridesync/src

COPY --from=frontend-build /app/frontend/dist ./ridesync-frontend/dist

WORKDIR /app/ridesync

RUN mvn -B -DskipTests package

# Production Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=backend-build /app/ridesync/target/ridesync-v2.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
