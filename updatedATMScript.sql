CREATE DATABASE IF NOT EXISTS atmDB;
USE atmDB;

CREATE TABLE Customers (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE DebitCards (
    card_number VARCHAR(16) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    pin_hash VARBINARY(64) NOT NULL, -- For storing a SHA-256 hash
    CONSTRAINT pk_card_number PRIMARY KEY (card_number),
    CONSTRAINT fk_card_customer FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT NOT NULL,
    transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES Accounts(account_id) ON DELETE CASCADE
);

-- Inserts that comply with the foreign key constraints
-- Customer 'Alice'
INSERT INTO Customers (name) VALUES ('Alice');
SET @alice_id = LAST_INSERT_ID();

-- Customer 'Bob'
INSERT INTO Customers (name) VALUES ('Bob');
SET @bob_id = LAST_INSERT_ID();

-- Alice's Account
INSERT INTO Accounts (customer_id, account_number, balance) VALUES (@alice_id, '00001', 1000.00);
INSERT INTO Accounts (customer_id, account_number, balance) VALUES (@alice_id, '00003', 10000.00);

-- Bob's Account
INSERT INTO Accounts (customer_id, account_number, balance) VALUES (@bob_id, '00002', 1500.00);
INSERT INTO Accounts (customer_id, account_number, balance) VALUES (@bob_id, '00004', 25000.00);

-- Alice's Debit Card
INSERT INTO DebitCards (card_number, customer_id, pin_hash) VALUES ('1111222233334444', @alice_id, UNHEX(SHA2('1234', 256)));

-- Bob's Debit Card
INSERT INTO DebitCards (card_number, customer_id, pin_hash) VALUES ('2222333344445555', @bob_id, UNHEX(SHA2('4321', 256)));
