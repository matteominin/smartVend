# smartVend

## Requisiti

- [Docker](https://www.docker.com/get-started)
- [Java](https://adoptium.net/)
- [Maven](https://maven.apache.org/)

## Avvio del Database

Per avviare il database PostgreSQL tramite Docker Compose:

```sh
docker-compose up
```

Questo comando avvierà un container PostgreSQL accessibile sulla porta `5432` con le seguenti credenziali (vedi [`docker-compose.yml`](docker-compose.yml)):
- **Database:** smartvend
- **User:** user
- **Password:** password

## Build ed Esecuzione dell'Applicazione

Per compilare ed eseguire l'applicazione Java:

```sh
mvn clean package
java -cp target/smartVend-1.0-SNAPSHOT.jar com.smartVend.app.Main
```

## Test

Per eseguire i test:

```sh
mvn test
```