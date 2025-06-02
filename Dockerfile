# Stage 1: build Java app
FROM maven:3.9.9-eclipse-temurin-24-alpine AS build
WORKDIR /app
COPY . .
RUN mvn -q clean package -DskipTests

# Stage 2: run Java app
FROM eclipse-temurin:24-jre
WORKDIR /app
# --- CHANGE THIS LINE ---
COPY --from=build /app/target/smartVend-1.0-SNAPSHOT.jar /app/app.jar
ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -DDB_URL=$DB_URL -DDB_USER=$DB_USER -DDB_PASS=$DB_PASS -jar /app/app.jar"]