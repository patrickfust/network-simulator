# Stage 1: Build the Angular application
FROM node:24 AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./frontend/
RUN cd frontend && npm install
COPY frontend ./frontend
RUN cd frontend && npm run build --prod

# Stage 2: Build the Spring Boot application
FROM maven:4.0.0-rc-5-eclipse-temurin-25 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./backend/
RUN cd backend && mvn dependency:go-offline
COPY backend ./backend
# Copy Angular build output directly to static directory
COPY --from=frontend-build /app/frontend/dist/network-simulator-frontend/* ./backend/src/main/resources/static/
RUN cd backend && mvn package -DskipTests

# Stage 3: Create the final image
FROM eclipse-temurin:25.0.1_8-jre-noble
WORKDIR /app

# Create a non-root user
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy the application JAR
COPY --from=backend-build /app/backend/target/*.jar app.jar

# Set permissions and switch to non-root user
RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 9898
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:h2}", "-jar", "app.jar"]
