package com.securebank.dao;

import com.securebank.model.Transaction;
import com.securebank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Module V: JDBC Data Access Object for transactions table.
 * Demonstrates:
 * - PreparedStatement parameter mapping
 * - Insertion within an ongoing atomic JDBC transaction
 */
public class TransactionDAO {

    /**
     * Inserts a transaction record using an existing database connection.
     * Essential for atomic multi-statement transfers.
     */
    public boolean insert(Connection conn, Transaction txn) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, account_id, type, amount, related_account_number, remark, balance_after, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, txn.getTransactionId());
            ps.setInt(2, txn.getAccountId());
            ps.setString(3, txn.getType());
            ps.setDouble(4, txn.getAmount());
            ps.setString(5, txn.getRelatedAccountNumber());
            ps.setString(6, txn.getRemark());
            ps.setDouble(7, txn.getBalanceAfter());
            ps.setTimestamp(8, txn.getCreatedAt() != null ? txn.getCreatedAt() : new Timestamp(System.currentTimeMillis()));

            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Inserts a transaction record using an independent connection.
     */
    public boolean insert(Transaction txn) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            return insert(conn, txn);
        } finally {
            DBConnection.close(conn, null);
        }
    }

    /**
     * Module IV: Returns all transactions for an account in an ArrayList.
     */
    public List<Transaction> findByAccountId(int accountId) throws SQLException {
        String sql = "SELECT transaction_id, account_id, type, amount, related_account_number, remark, balance_after, created_at " +
                     "FROM transactions WHERE account_id = ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Transaction> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
            return list;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Returns the most recent N transactions for an account (used on User Dashboard).
     */
    public List<Transaction> findRecentByAccountId(int accountId, int limit) throws SQLException {
        String sql = "SELECT transaction_id, account_id, type, amount, related_account_number, remark, balance_after, created_at " +
                     "FROM transactions WHERE account_id = ? ORDER BY created_at DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Transaction> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ps.setInt(2, limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapRowToTransaction(rs));
            }
            return list;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Finds a single transaction by its unique ID.
     */
    public Transaction findByTransactionId(String transactionId) throws SQLException {
        String sql = "SELECT transaction_id, account_id, type, amount, related_account_number, remark, balance_after, created_at " +
                     "FROM transactions WHERE transaction_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, transactionId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToTransaction(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("transaction_id"),
                rs.getInt("account_id"),
                rs.getString("type"),
                rs.getDouble("amount"),
                rs.getString("related_account_number"),
                rs.getString("remark"),
                rs.getDouble("balance_after"),
                rs.getTimestamp("created_at")
        );
    }
}

