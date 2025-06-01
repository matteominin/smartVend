#!/bin/bash

# Load .env variables
export $(grep -v '^#' .env | xargs)

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
  echo "🚀 Starting Docker..."
  open --background -a Docker || sudo systemctl start docker
  echo "⏳ Waiting for Docker to start..."
  while ! docker info > /dev/null 2>&1; do
    sleep 1
  done
fi

# Start containers (in detached mode)
echo "📦 Starting containers..."
docker-compose up -d

# Wait for Postgres to be ready
echo "⏳ Waiting for PostgreSQL to be ready..."
until docker exec smartvend-db pg_isready -U "$DB_USER" > /dev/null 2>&1; do
  sleep 1
done

echo "✅ PostgreSQL is ready."

# Run the Java application
echo "▶️ Running Java app..."

mvn -q clean compile exec:java \
  -Dexec.mainClass="com.smartvend.app.Main" \
  -DDB_URL=$DB_URL \
  -DDB_USER=$DB_USER \
  -DDB_PASS=$DB_PASS \
  -Dorg.slf4j.simpleLogger.defaultLogLevel=off \
  -Djava.util.logging.config.file=logging.properties
