package com.securebank.model;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Module I & II: Model class representing a financial transaction.
 */
public class Transaction {

    public static final String TYPE_DEPOSIT = "DEPOSIT";
    public static final String TYPE_WITHDRAW = "WITHDRAW";
    public static final String TYPE_TRANSFER_OUT = "TRANSFER_OUT";
    public static final String TYPE_TRANSFER_IN = "TRANSFER_IN";

    private String transactionId;
    private int accountId;
    private String type;
    private double amount;
    private String relatedAccountNumber;
    private String remark;
    private double balanceAfter;
    private Timestamp createdAt;

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("##,##,##0.00");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public Transaction() {
    }

    public Transaction(String transactionId, int accountId, String type, double amount, 
                       String relatedAccountNumber, String remark, double balanceAfter, Timestamp createdAt) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.relatedAccountNumber = relatedAccountNumber;
        this.remark = remark;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getFormattedAmount() {
        return "Rs. " + CURRENCY_FORMAT.format(amount);
    }

    public String getRelatedAccountNumber() { return relatedAccountNumber; }
    public void setRelatedAccountNumber(String relatedAccountNumber) { this.relatedAccountNumber = relatedAccountNumber; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public double getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(double balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getFormattedBalanceAfter() {
        return "Rs. " + CURRENCY_FORMAT.format(balanceAfter);
    }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getFormattedDate() {
        if (createdAt == null) return "-";
        return DATE_FORMAT.format(createdAt);
    }

    public boolean isCredit() {
        return TYPE_DEPOSIT.equalsIgnoreCase(type) || TYPE_TRANSFER_IN.equalsIgnoreCase(type);
    }
}

