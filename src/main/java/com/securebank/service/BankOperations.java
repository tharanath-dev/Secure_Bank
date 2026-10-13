package com.securebank.service;

import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.InactiveAccountException;
import com.securebank.exception.InsufficientBalanceException;
import com.securebank.exception.InvalidAmountException;
import com.securebank.model.Transaction;

/**
 * Module II: Interface Inheritance & Nested Interface
 * 
 * - Demonstrates interface extending another interface ('extends BaseOperations')
 * - Demonstrates nested interface ('public interface AuditCallback')
 */
public interface BankOperations extends BaseOperations {

    // =========================================================================
    // Module II: Nested Interface
    // Used for event notification callbacks after banking operations.
    // =========================================================================
    interface AuditCallback {
        void onOperationCompleted(String operation, String details);
    }

    /**
     * Deposits money into an active bank account.
     */
    Transaction deposit(String accountNumber, double amount, String remark)
            throws AccountNotFoundException, InactiveAccountException, InvalidAmountException;

    /**
     * Withdraws money from an active bank account subject to minimum balance requirements.
     */
    Transaction withdraw(String accountNumber, double amount, String remark)
            throws AccountNotFoundException, InactiveAccountException, InvalidAmountException, InsufficientBalanceException;

    /**
     * Executes atomic funds transfer between two accounts using ACID transaction management.
     */
    void transfer(String fromAccountNumber, String toAccountNumber, double amount, String remark)
            throws AccountNotFoundException, InactiveAccountException, InvalidAmountException, InsufficientBalanceException;
}

