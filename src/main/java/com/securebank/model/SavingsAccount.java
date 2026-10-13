package com.securebank.model;

import java.sql.Timestamp;

/**
 * Module II: Runtime Polymorphism (Dynamic Method Dispatch)
 * Savings account implementation with mandatory minimum balance requirement.
 */
public class SavingsAccount extends Account {

    public static final double MIN_BALANCE = 1000.00;

    public SavingsAccount() {
        super();
        setAccountType("SAVINGS");
    }

    public SavingsAccount(int accountId, int userId, String accountNumber, double balance, String status, Timestamp createdAt) {
        super(accountId, userId, accountNumber, "SAVINGS", balance, status, createdAt);
    }

    /**
     * Module II: Overridden method implementing polymorphic minimum balance rule.
     */
    @Override
    public double getMinimumBalance() {
        return MIN_BALANCE;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }
}

