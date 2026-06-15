CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance NUMERIC(15,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,

    CONSTRAINT fk_account_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE atm_cash (
    id BIGSERIAL PRIMARY KEY,
    currency VARCHAR(3) NOT NULL,
    denomination INT NOT NULL,
    quantity INT NOT NULL
);

CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    from_currency VARCHAR(3),
    to_currency VARCHAR(3),
    rate NUMERIC(15,6)
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,

    type VARCHAR(20) NOT NULL,

    from_account_id BIGINT,
    to_account_id BIGINT,

    amount NUMERIC(15,2) NOT NULL,

    from_currency VARCHAR(3),
    to_currency VARCHAR(3),

    exchange_rate NUMERIC(15,6),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);