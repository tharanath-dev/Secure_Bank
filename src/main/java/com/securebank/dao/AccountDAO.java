package com.securebank.dao;

import com.securebank.model.Account;
import com.securebank.model.CurrentAccount;
import com.securebank.model.SavingsAccount;
import com.securebank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Module V: JDBC Data Access Object for accounts table.
 * Demonstrates:
 * - Instantiating concrete polymorphic subclasses (SavingsAccount vs CurrentAccount)
 * - Transactional PreparedStatement over an existing Connection
 */
public class AccountDAO {

    /**
     * Creates an account within an existing JDBC transaction connection.
     */
    public int create(Connection conn, Account account) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, account_number, account_type, balance, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getAccountNumber());
            ps.setString(3, account.getAccountType());
            ps.setDouble(4, account.getBalance());
            ps.setString(5, account.getStatus() != null ? account.getStatus() : Account.STATUS_ACTIVE);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                account.setAccountId(generatedId);
                return generatedId;
            }
            throw new SQLException("Failed to create account, no ID obtained.");
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    /**
     * Creates an account with an independent connection.
     */
    public int create(Account account) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            return create(conn, account);
        } finally {
            DBConnection.close(conn, null);
        }
    }

    /**
     * Finds an account by owner user_id.
     */
    public Account findByUserId(int userId) throws SQLException {
        String sql = "SELECT account_id, user_id, account_number, account_type, balance, status, created_at FROM accounts WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToAccount(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Finds an account by unique 12-digit account_number.
     */
    public Account findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT account_id, user_id, account_number, account_type, balance, status, created_at FROM accounts WHERE account_number = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, accountNumber);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToAccount(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Finds an account by account_id.
     */
    public Account findById(int accountId) throws SQLException {
        String sql = "SELECT account_id, user_id, account_number, account_type, balance, status, created_at FROM accounts WHERE account_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToAccount(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Updates account balance within an active JDBC transaction connection.
     */
    public boolean updateBalance(Connection conn, int accountId, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_id = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, newBalance);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) ps.close();
        }
    }

    /**
     * Updates account balance with an independent connection.
     */
    public boolean updateBalance(int accountId, double newBalance) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            return updateBalance(conn, accountId, newBalance);
        } finally {
            DBConnection.close(conn, null);
        }
    }

    /**
     * Toggles or updates account status (ACTIVE / INACTIVE).
     */
    public boolean updateStatus(int accountId, String status) throws SQLException {
        String sql = "UPDATE accounts SET status = ? WHERE account_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    public int countAccounts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    public int countActiveAccounts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE status = 'ACTIVE'";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    public int countInactiveAccounts() throws SQLException {
        String sql = "SELECT COUNT(*) FROM accounts WHERE status = 'INACTIVE'";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    public List<Account> findAllAccounts() throws SQLException {
        String sql = "SELECT account_id, user_id, account_number, account_type, balance, status, created_at FROM accounts";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Account> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRowToAccount(rs));
            }
            return list;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Module II: Concrete instantiation based on polymorphic type column.
     */
    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        int accountId = rs.getInt("account_id");
        int userId = rs.getInt("user_id");
        String accountNumber = rs.getString("account_number");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        String status = rs.getString("status");
        Timestamp createdAt = rs.getTimestamp("created_at");

        if ("CURRENT".equalsIgnoreCase(accountType)) {
            return new CurrentAccount(accountId, userId, accountNumber, balance, status, createdAt);
        } else {
            return new SavingsAccount(accountId, userId, accountNumber, balance, status, createdAt);
        }
    }
}

