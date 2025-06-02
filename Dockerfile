# Stage 1: build Java app
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -q clean package -DskipTests

# Stage 2: run Java app
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/smartVend-1.0-SNAPSHOT.jar /app/app.jar
ENV JAVA_OPTS=""
CMD java $JAVA_OPTS \
    -DDB_URL=$DB_URL \
    -DDB_USER=$DB_USER \
    -DDB_PASS=$DB_PASS \
    -Dorg.slf4j.simpleLogger.defaultLogLevel=off \
    -jar /app/app.jar
