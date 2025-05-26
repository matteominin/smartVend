# ☕ Macchinetta del Caffè - Vending Machine Project

> **Gestione completa di un sistema di macchine del caffè con utenti, prodotti, transazioni, manutenzione e dashboard admin.**

---

## 🚀 Funzionalità principali

### 🔧 Utente (Cliente)

* Registrazione e login
* Visualizzazione prodotti
* Acquisto di prodotti
* Ricarica saldo
* Visualizzazione delle proprie transazioni

### 📄 Admin

* Gestione utenti (disattivazione)
* Aggiunta, modifica e cancellazione di prodotti
* Gestione distributori automatici

### 👷 Operatore (Worker)

* Registrazione degli errori rilevati
* Assegnazione e completamento manutenzioni
* Visualizzazione dei log di manutenzione per macchina o stato

---

## 📚 Struttura del progetto

```
src/main/java/com/vending
├── controller       # Controller (AdminController, CustomerController, WorkerController)
├── service          # Business Logic (UserService, ItemService, MaintenanceService...)
├── dao              # DAO Interface & Implementation
├── model            # Entity: User, Item, VendingMachine, Transaction...
├── util             # Utility (DatabaseConnection, PasswordUtils, DateTimeUtils)
└── exception        # Custom exceptions (DatabaseException)
```

---

## 🌐 Tecnologie utilizzate

| Tecnologia       | Ruolo                    |
| ---------------- | ------------------------ |
| Java 17+         | Linguaggio principale    |
| PostgreSQL       | Database relazionale     |
| JUnit + Mockito  | Test automatizzati       |
| Docker + Compose | Container per DB e setup |
| Jacoco           | Code coverage dei test   |

---

## 🌐 Setup del progetto con Docker

### 1. Requisiti

* [Docker](https://www.docker.com/get-started)
* [Docker Compose](https://docs.docker.com/compose/)

### 2. Struttura

Assicurati che il progetto contenga:

```
project-root/
├── docker-compose.yml
├── db/
│   └── schema.sql
```

### 3. Avvio del database

```bash
docker-compose up
```

> Questo comando avvia un container PostgreSQL e inizializza automaticamente il database `macchinetta_db` usando `db/schema.sql`

Il database sarà accessibile su `localhost:5432`

---

## 🔬 Test del progetto

### 1. Esecuzione dei test

```bash
mvn clean test
```

### 2. Generazione report Jacoco

```bash
mvn jacoco:report
```

> Il report HTML sarà generato in `target/site/jacoco/index.html`

### 3. Cosa viene testato

* DAO (accesso a DB reale)
* Service (logica applicativa)
* Controller (mock dei service con Mockito)

---

## 📅 UML e Diagrammi

* Class Diagram (Controller, DAO, Service)
* Use Case Diagram (Admin, Customer, Worker)
* DB Diagram (relazioni tra tabelle PostgreSQL)

> I diagrammi sono disponibili nella cartella `/uml` oppure come immagini `.jpeg`

---

## 🚨 Note importanti

* ❌ Non è necessaria un'installazione locale di PostgreSQL
* ✅ Tutti i dati di test sono inseriti nei test JUnit o via script
* ❗ Il `main()` è solo una demo testuale, non rappresenta il sistema reale

---

## 🏆 Obiettivi raggiunti

* [x] Test JUnit per DAO e Service
* [x] Mock dei Controller con Mockito
* [x] Setup automatico DB con Docker
* [x] Copertura test con Jacoco
* [x] Progetto documentato e organizzato

---

## 🎮 Avvio rapido

```bash
git clone https://github.com/SimonePell/SWE.git
cd SWE
docker-compose up
mvn clean test
mvn exec:java
```

---

## 🙌 Autori

* Simone Suma
* Simone Pellicci
* Matteo Minin

---

## 🚫 Licenza

MIT — libero utilizzo per scopi educativi e dimostrativi.