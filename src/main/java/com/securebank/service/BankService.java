package com.securebank.service;

import com.securebank.dao.AccountDAO;
import com.securebank.dao.TransactionDAO;
import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.InactiveAccountException;
import com.securebank.exception.InsufficientBalanceException;
import com.securebank.exception.InvalidAmountException;
import com.securebank.model.Account;
import com.securebank.model.Transaction;
import com.securebank.util.DBConnection;
import com.securebank.util.FileAuditLogger;
import com.securebank.util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Module II & III: Service Layer implementing BankOperations.
 * 
 * Demonstrates:
 * - Module II: Interface implementation & nested interface usage
 * - Module III: Multithreading synchronization on monetary transactions to prevent race conditions
 * - Module III: Custom checked exceptions handling with multiple catch blocks and rollback
 * - Module IV: HashMap for fast account caching
 * - Module V: JDBC transaction management (setAutoCommit(false), commit(), rollback())
 */
public class BankService implements BankOperations {

    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    // Module IV: HashMap lookup collection for recent account balance caching
    private final Map<String, Account> accountCache = new HashMap<>();

    // Module II: Nested interface instance
    private final AuditCallback auditCallback;

    public BankService() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        // Module II: Anonymous class or lambda implementing nested interface
        this.auditCallback = (operation, details) -> {
            FileAuditLogger.logAction(operation, "SYSTEM", details);
        };
    }

    /**
     * Module II: Real-time balance check from BaseOperations interface.
     */
    @Override
    public double checkBalance(String accountNumber) throws AccountNotFoundException, InactiveAccountException {
        Account account = getValidAccount(accountNumber);
        return account.getBalance();
    }

    /**
     * Module III: Synchronized method to prevent race conditions during concurrent deposits.
     */
    @Override
    public synchronized Transaction deposit(String accountNumber, double amount, String remark)
            throws AccountNotFoundException, InactiveAccountException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be strictly greater than zero.");
        }

        Account account = getValidAccount(accountNumber);

        // Perform balance calculation
        double newBalance = account.getBalance() + amount;
        String finalRemark = (remark != null && !remark.trim().isEmpty()) ? remark.trim() : "Cash Deposit";
        String txnId = PasswordUtil.generateTransactionId();

        try {
            boolean updated = accountDAO.updateBalance(account.getAccountId(), newBalance);
            if (!updated) {
                throw new SQLException("Failed to update account balance in database.");
            }

            Transaction txn = new Transaction(
                    txnId,
                    account.getAccountId(),
                    Transaction.TYPE_DEPOSIT,
                    amount,
                    null,
                    finalRemark,
                    newBalance,
                    new Timestamp(System.currentTimeMillis())
            );

            transactionDAO.insert(txn);

            // Update in-memory account state and cache
            account.setBalance(newBalance);
            accountCache.put(accountNumber, account);

            // Audit log & callback
            auditCallback.onOperationCompleted("DEPOSIT", "Account " + accountNumber + " credited with Rs. " + amount);

            return txn;
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred during deposit operation: " + e.getMessage(), e);
        }
    }

    /**
     * Module III: Synchronized method to prevent race conditions during concurrent withdrawals.
     */
    @Override
    public synchronized Transaction withdraw(String accountNumber, double amount, String remark)
            throws AccountNotFoundException, InactiveAccountException, InvalidAmountException, InsufficientBalanceException {

        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be strictly greater than zero.");
        }

        Account account = getValidAccount(accountNumber);

        // Module II: Polymorphic rule check (SavingsAccount vs CurrentAccount minimum balance)
        if (!account.canWithdraw(amount)) {
            double minRequired = account.getMinimumBalance();
            throw new InsufficientBalanceException(String.format(
                    "Insufficient funds. Maximum withdrawable amount is Rs. %.2f (Minimum required balance: Rs. %.2f).",
                    Math.max(0, account.getBalance() - minRequired), minRequired));
        }

        double newBalance = account.getBalance() - amount;
        String finalRemark = (remark != null && !remark.trim().isEmpty()) ? remark.trim() : "Cash Withdrawal";
        String txnId = PasswordUtil.generateTransactionId();

        try {
            boolean updated = accountDAO.updateBalance(account.getAccountId(), newBalance);
            if (!updated) {
                throw new SQLException("Failed to update account balance in database.");
            }

            Transaction txn = new Transaction(
                    txnId,
                    account.getAccountId(),
                    Transaction.TYPE_WITHDRAW,
                    amount,
                    null,
                    finalRemark,
                    newBalance,
                    new Timestamp(System.currentTimeMillis())
            );

            transactionDAO.insert(txn);

            account.setBalance(newBalance);
            accountCache.put(accountNumber, account);

            auditCallback.onOperationCompleted("WITHDRAW", "Account " + accountNumber + " debited with Rs. " + amount);

            return txn;
        } catch (SQLException e) {
            throw new RuntimeException("Database error occurred during withdrawal: " + e.getMessage(), e);
        }
    }

    /**
     * Module III & V: Synchronized Atomic Funds Transfer with ACID compliance.
     * Demonstrates setAutoCommit(false), commit(), rollback(), and multiple catch blocks.
     */
    @Override
    public synchronized void transfer(String fromAccountNumber, String toAccountNumber, double amount, String remark)
            throws AccountNotFoundException, InactiveAccountException, InvalidAmountException, InsufficientBalanceException {

        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be strictly greater than zero.");
        }

        if (fromAccountNumber != null && fromAccountNumber.equalsIgnoreCase(toAccountNumber)) {
            throw new InvalidAmountException("Sender and beneficiary account numbers cannot be identical.");
        }

        Account sender = getValidAccount(fromAccountNumber);
        Account receiver = getValidAccount(toAccountNumber);

        // Module II: Polymorphic minimum balance validation on sender
        if (!sender.canWithdraw(amount)) {
            double minRequired = sender.getMinimumBalance();
            throw new InsufficientBalanceException(String.format(
                    "Transfer failed: insufficient funds. Maintain minimum balance of Rs. %.2f.", minRequired));
        }

        Connection conn = null;
        String debitTxnId = PasswordUtil.generateTransactionId();
        String creditTxnId = PasswordUtil.generateTransactionId();
        String finalRemark = (remark != null && !remark.trim().isEmpty()) ? remark.trim() : "Funds Transfer";

        try {
            conn = DBConnection.getConnection();
            // Module V: Start manual JDBC transaction
            conn.setAutoCommit(false);

            double senderNewBalance = sender.getBalance() - amount;
            double receiverNewBalance = receiver.getBalance() + amount;

            // 1. Debit sender
            accountDAO.updateBalance(conn, sender.getAccountId(), senderNewBalance);

            // 2. Credit receiver
            accountDAO.updateBalance(conn, receiver.getAccountId(), receiverNewBalance);

            // 3. Record sender outgoing transaction
            Transaction outTxn = new Transaction(
                    debitTxnId,
                    sender.getAccountId(),
                    Transaction.TYPE_TRANSFER_OUT,
                    amount,
                    receiver.getAccountNumber(),
                    finalRemark + " to " + receiver.getAccountNumber(),
                    senderNewBalance,
                    new Timestamp(System.currentTimeMillis())
            );
            transactionDAO.insert(conn, outTxn);

            // 4. Record receiver incoming transaction
            Transaction inTxn = new Transaction(
                    creditTxnId,
                    receiver.getAccountId(),
                    Transaction.TYPE_TRANSFER_IN,
                    amount,
                    sender.getAccountNumber(),
                    finalRemark + " from " + sender.getAccountNumber(),
                    receiverNewBalance,
                    new Timestamp(System.currentTimeMillis())
            );
            transactionDAO.insert(conn, inTxn);

            // Module V: Commit the atomic transaction
            conn.commit();

            // Update in-memory state
            sender.setBalance(senderNewBalance);
            receiver.setBalance(receiverNewBalance);
            accountCache.put(sender.getAccountNumber(), sender);
            accountCache.put(receiver.getAccountNumber(), receiver);

            auditCallback.onOperationCompleted("TRANSFER",
                    String.format("Transfer of Rs. %.2f from %s to %s succeeded [Txn: %s]",
                            amount, fromAccountNumber, toAccountNumber, debitTxnId));

        } catch (SQLException e) {
            // Module V & III: Rollback on database failure
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[BankService] Transaction rolled back due to error: " + e.getMessage());
                } catch (SQLException rollbackEx) {
                    System.err.println("[BankService] Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Database error during transfer operation. Transaction rolled back: " + e.getMessage(), e);
        } finally {
            // Module V: Restore auto-commit and close connection
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Retrieves account by number, verifying existence and ACTIVE status.
     */
    public Account getValidAccount(String accountNumber) throws AccountNotFoundException, InactiveAccountException {
        try {
            Account account = accountDAO.findByAccountNumber(accountNumber);
            if (account == null) {
                throw new AccountNotFoundException("Account number " + accountNumber + " does not exist.");
            }
            if (!account.isActive()) {
                throw new InactiveAccountException("Account number " + accountNumber + " is deactivated or inactive.");
            }
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching account: " + e.getMessage(), e);
        }
    }

    public Account getAccountByUserId(int userId) throws SQLException {
        return accountDAO.findByUserId(userId);
    }

    public Account getAccountByNumber(String accountNumber) throws SQLException {
        return accountDAO.findByAccountNumber(accountNumber);
    }

    public List<Transaction> getTransactions(int accountId) throws SQLException {
        return transactionDAO.findByAccountId(accountId);
    }

    public List<Transaction> getRecentTransactions(int accountId, int limit) throws SQLException {
        return transactionDAO.findRecentByAccountId(accountId, limit);
    }
}

