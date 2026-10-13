package com.securebank.dao;

import com.securebank.model.PasswordResetToken;
import com.securebank.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Module V: JDBC Data Access Object for password_reset_tokens table.
 */
public class TokenDAO {

    /**
     * Saves a new password reset token.
     */
    public boolean saveToken(int userId, String tokenHash, Timestamp expiryTime) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens (user_id, token_hash, expiry_time, used, created_at) VALUES (?, ?, ?, FALSE, ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, tokenHash);
            ps.setTimestamp(3, expiryTime);
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    /**
     * Finds a valid, unexpired, unused token by its hash.
     */
    public PasswordResetToken findByHash(String tokenHash) throws SQLException {
        String sql = "SELECT token_id, user_id, token_hash, expiry_time, used, created_at FROM password_reset_tokens " +
                     "WHERE token_hash = ? AND used = FALSE AND expiry_time > ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, tokenHash);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            rs = ps.executeQuery();

            if (rs.next()) {
                return new PasswordResetToken(
                        rs.getInt("token_id"),
                        rs.getInt("user_id"),
                        rs.getString("token_hash"),
                        rs.getTimestamp("expiry_time"),
                        rs.getBoolean("used"),
                        rs.getTimestamp("created_at")
                );
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Marks a reset token as used.
     */
    public boolean markUsed(int tokenId) throws SQLException {
        String sql = "UPDATE password_reset_tokens SET used = TRUE WHERE token_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, tokenId);
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    /**
     * Module III Multithreading: Called periodically by the background cleanup thread
     * to purge expired or already used tokens.
     */
    public int deleteExpiredOrUsedTokens() throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE used = TRUE OR expiry_time <= ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            return ps.executeUpdate();
        } finally {
            DBConnection.close(conn, ps);
        }
    }
}

