# Bank of CLI

Bank of CLI is a Java command-line banking application that allows users to create accounts, log in, manage balances, transfer money, and view transaction history.

The project uses a layered architecture with separate API, service, and repository layers.

---

## Features

- Register a new account
- Login using Account ID and PIN
- Check account balance
- Deposit money
- Withdraw money
- Prevent overdrawing
- Transfer money between accounts
- View recent transaction history
- Log successful actions and errors
- Store account and transaction data in PostgreSQL

---

## Tech Stack

- Java 21
- PostgreSQL
- Docker
- JDBC
- Maven
- JUnit 5
- Mockito
- SLF4J
- Logback
- Git & GitHub

---

## Architecture

```text
CLI / API Layer
       ↓
Service Layer
       ↓
Repository Layer
       ↓
JDBC
       ↓
PostgreSQL
       ↓
Docker