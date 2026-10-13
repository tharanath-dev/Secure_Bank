package com.securebank.model;

import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * Module II: Polymorphism (Abstract class, Dynamic Method Dispatch, Method Overloading)
 * and Nested Classes (Inner Class & Static Nested Class).
 * 
 * Base abstract account class for all banking accounts.
 */
public abstract class Account {

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";

    private int accountId;
    private int userId;
    private String accountNumber;
    private String accountType; // 'SAVINGS' or 'CURRENT'
    private double balance;
    private String status;      // 'ACTIVE' or 'INACTIVE'
    private Timestamp createdAt;

    // Currency formatter
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("##,##,##0.00");

    public Account() {
    }

    public Account(int accountId, int userId, String accountNumber, String accountType, double balance, String status, Timestamp createdAt) {
        this.accountId = accountId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Module II: Abstract method defining runtime polymorphism
    public abstract double getMinimumBalance();

    public abstract String getAccountType();

    /**
     * Module II: Runtime Polymorphic rule enforcement.
     * Determines whether withdrawal of 'amount' satisfies minimum balance requirement.
     */
    public boolean canWithdraw(double amount) {
        if (!isActive()) {
            return false;
        }
        return (this.balance - amount) >= getMinimumBalance();
    }

    public boolean isActive() {
        return STATUS_ACTIVE.equalsIgnoreCase(this.status);
    }

    // =========================================================================
    // Module II: Method Overloading (Compile-Time Polymorphism)
    // =========================================================================

    /**
     * Overloaded method 1: Deposit with amount only.
     */
    public void deposit(double amount) {
        this.deposit(amount, "Cash Deposit");
    }

    /**
     * Overloaded method 2: Deposit with amount and custom remark.
     */
    public void deposit(double amount, String remark) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (canWithdraw(amount) && amount > 0) {
            this.balance -= amount;
            return true;
        }
        return false;
    }

    // Getters and Setters
    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getFormattedBalance() {
        return "Rs. " + CURRENCY_FORMAT.format(balance);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // =========================================================================
    // Module II: Inner Class (Non-static Nested Class)
    // Has implicit access to enclosing Account instance fields.
    // =========================================================================
    public class Receipt {
        private String transactionId;
        private String transactionType;
        private double amount;
        private Timestamp timestamp;
        private double postBalance;

        public Receipt(String transactionId, String transactionType, double amount, Timestamp timestamp, double postBalance) {
            this.transactionId = transactionId;
            this.transactionType = transactionType;
            this.amount = amount;
            this.timestamp = timestamp;
            this.postBalance = postBalance;
        }

        public String getEnclosingAccountNumber() {
            // Demonstrates Account.this syntax accessing enclosing class instance
            return Account.this.getAccountNumber();
        }

        public String getFormattedDetails() {
            return String.format("Receipt [TXN: %s | Account: %s (%s) | %s: Rs. %.2f | Balance: Rs. %.2f]",
                    transactionId, Account.this.accountNumber, Account.this.accountType,
                    transactionType, amount, postBalance);
        }

        public String getTransactionId() { return transactionId; }
        public String getTransactionType() { return transactionType; }
        public double getAmount() { return amount; }
        public Timestamp getTimestamp() { return timestamp; }
        public double getPostBalance() { return postBalance; }
    }

    // =========================================================================
    // Module II: Static Nested Class
    // Can be instantiated without an enclosing Account instance.
    // =========================================================================
    public static class AccountSummary {
        private String accountNumber;
        private String customerName;
        private String accountType;
        private double balance;
        private String status;

        public AccountSummary(String accountNumber, String customerName, String accountType, double balance, String status) {
            this.accountNumber = accountNumber;
            this.customerName = customerName;
            this.accountType = accountType;
            this.balance = balance;
            this.status = status;
        }

        public String getAccountNumber() { return accountNumber; }
        public String getCustomerName() { return customerName; }
        public String getAccountType() { return accountType; }
        public double getBalance() { return balance; }
        public String getStatus() { return status; }

        public String getFormattedBalance() {
            return "Rs. " + CURRENCY_FORMAT.format(balance);
        }
    }
}

