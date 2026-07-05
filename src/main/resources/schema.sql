CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    account_id VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
