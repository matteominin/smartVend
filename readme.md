# 🚀 JavaBrew

## Quick Start (Cross-platform: Windows, macOS, Linux)

### 1. Clone the Repository

Clone the project and navigate into the folder:

```sh
git clone https://github.com/matteominin/smartVend.git
cd smartVend
```

---

### 2. Environment Variables

Copy the example environment file to `.env`:

```sh
cp .env.example .env
```

On Windows, create a file named `.env` and copy-paste the following:

```env
DB_NAME=javabrew
DB_USER=postgres
DB_PASS=postgres
```

Modify the values as needed (for your own database credentials, if necessary).

---

### 3. Start Database and Adminer

Start the **PostgreSQL database** and the Adminer web UI (for database browsing):

```sh
docker compose up -d db adminer
```

* The database will be available at port `5434`
* Adminer UI will be available at [http://localhost:8081](http://localhost:8081)

---

### 4. Run the Application (Interactive CLI)

Run the Java CLI application **with interactive console support**:

```sh
docker compose run --rm app
```

* This command allows you to interact with the application and enter your credentials.

---

### 5. Stop All Services

When you’re done, stop everything with:

```sh
docker compose down
```

---
