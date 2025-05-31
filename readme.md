## Avvio dell'applicazione

1. **Copia il file di esempio delle variabili d'ambiente:**

   ```sh
   cp .env.example .env
   ```

   Modifica il file `.env` secondo le tue esigenze (ad esempio, credenziali del database).

2. **Avvia tutti i servizi (database, backend, ecc.) con Docker Compose:**

   ```sh
   docker-compose up
   ```

   Questo comando utilizzerà le variabili definite in `.env` per configurare i container.

3. **Per fermare i servizi:**

   ```sh
   docker-compose down
   ```

Assicurati di aver creato e configurato il file `.env` prima di avviare Docker Compose.