CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    owner_name VARCHAR(100) NOT NULL,
    pin_hash VARCHAR(255) NOT NULL,
    balance NUMERIC(12, 2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    account_id INT REFERENCES accounts(account_id),
    type VARCHAR(20) NOT NULL,
    amount NUMERIC(12, 2) NOT NULL CHECK (amount > 0),
    related_account_id INT REFERENCES accounts(account_id),
    created_at TIMESTAMP DEFAULT NOW()
);