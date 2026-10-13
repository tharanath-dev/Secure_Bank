-- =====================================================================
-- SecureBank Database Schema
-- Database: MySQL 8.0+
-- Description: Clean, unseeded schema for SecureBank Banking Management System
-- NOTE: No dummy or fake data. All records are created dynamically by users.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS securebank_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE securebank_db;

-- Drop child tables first to respect foreign key constraints
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;

-- 1. Users table (stores credentials and role for Customer and Admin)
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    password_hash VARCHAR(64) NOT NULL,
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Accounts table (each user owns an account: SAVINGS or CURRENT)
CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    account_number VARCHAR(12) NOT NULL UNIQUE,
    account_type VARCHAR(20) NOT NULL, -- 'SAVINGS' or 'CURRENT'
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Transactions table (audit of all monetary activities)
CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    account_id INT NOT NULL,
    type ENUM('DEPOSIT', 'WITHDRAW', 'TRANSFER_OUT', 'TRANSFER_IN') NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    related_account_number VARCHAR(20) DEFAULT NULL,
    remark VARCHAR(255) DEFAULT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Password reset tokens table (for secure token-based password recovery)
CREATE TABLE password_reset_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expiry_time TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Helpful indexes for lookup performance
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_accounts_number ON accounts(account_number);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_tokens_hash ON password_reset_tokens(token_hash);

