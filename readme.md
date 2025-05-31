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

Questo comando avvierà un container PostgreSQL accessibile sulla porta `5432` con le seguenti credenziali (vedi [`docker-compose.yml`](docker-compose.yml)):z
- **Database:** smartvend
- **User:** user
- **Password:** password

## Avvio rapido con Docker Compose

Per avviare l'intera applicazione (database e backend) tramite Docker Compose, esegui:

```sh
docker-compose up
```

Questo comando avvierà sia il database PostgreSQL sia l'applicazione Java, secondo la configurazione presente in [`docker-compose.yml`](docker-compose.yml).  
L'applicazione sarà accessibile secondo le impostazioni delle porte definite nel file di compose.

Per fermare i servizi, premi `Ctrl+C` oppure esegui:

```sh
docker-compose down
```

## Test

Per eseguire i test:

```sh
mvn test
```