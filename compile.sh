#!/bin/bash

# Load .env variables
export $(grep -v '^#' .env | xargs)

mvn clean compile exec:java \
  -Dexec.mainClass="com.smartvend.app.Main" \
  -DDB_URL=$DB_URL \
  -DDB_USER=$DB_USER \
  -DDB_PASS=$DB_PASS