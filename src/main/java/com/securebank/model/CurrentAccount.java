package com.securebank.model;

import java.sql.Timestamp;

/**
 * Module II: Runtime Polymorphism (Dynamic Method Dispatch)
 * Current account implementation typically used by commercial entities with higher minimum balance.
 */
public class CurrentAccount extends Account {

    public static final double MIN_BALANCE = 5000.00;

    public CurrentAccount() {
        super();
        setAccountType("CURRENT");
    }

    public CurrentAccount(int accountId, int userId, String accountNumber, double balance, String status, Timestamp createdAt) {
        super(accountId, userId, accountNumber, "CURRENT", balance, status, createdAt);
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
        return "CURRENT";
    }
}

