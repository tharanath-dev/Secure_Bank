package com.securebank.dao;

import com.securebank.model.Admin;
import com.securebank.model.Customer;
import com.securebank.model.Person;
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
 * Module V: JDBC Data Access Object for users table.
 * Demonstrates:
 * - PreparedStatement parameter binding
 * - ResultSet traversal
 * - Resource cleanup in finally blocks
 * - Module III: Nested try-catch block for granular exception handling
 */
public class UserDAO {

    /**
     * Inserts a new user record and returns the generated user_id.
     */
    public int create(Person person) throws SQLException {
        String sql = "INSERT INTO users (name, email, phone, password_hash, role, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, person.getName());
            ps.setString(2, person.getEmail().trim().toLowerCase());
            ps.setString(3, person.getPhone());
            ps.setString(4, person.getPasswordHash());
            ps.setString(5, person.getRole());
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                person.setUserId(generatedId);
                return generatedId;
            }
            throw new SQLException("Creating user failed, no ID obtained.");
        } finally {
            // Module V & III: Safe closure in finally block
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Finds a user by email address.
     * Demonstrates Module III: Nested try-catch block.
     */
    public Person findByEmail(String email) throws SQLException {
        String sql = "SELECT user_id, name, email, phone, password_hash, role, created_at FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim().toLowerCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                // Module III: Nested try block for safe object instantiation
                try {
                    return mapRowToPerson(rs);
                } catch (Exception ex) {
                    System.err.println("[UserDAO] Nested try caught mapping error: " + ex.getMessage());
                    throw new SQLException("Error mapping database row to Person", ex);
                }
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Finds a user by unique user_id.
     */
    public Person findById(int userId) throws SQLException {
        String sql = "SELECT user_id, name, email, phone, password_hash, role, created_at FROM users WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            if (rs.next()) {
                return mapRowToPerson(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Checks if any admin exists in the database.
     */
    public int countAdmins() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Returns total count of registered customers.
     */
    public int countCustomers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'USER'";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Updates customer profile details (name and phone).
     */
    public boolean updateProfile(int userId, String name, String phone) throws SQLException {
        String sql = "UPDATE users SET name = ?, phone = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    /**
     * Updates user password hash.
     */
    public boolean updatePassword(int userId, String newPasswordHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } finally {
            DBConnection.close(conn, ps);
        }
    }

    /**
     * Module IV: Returns all registered customers in an ArrayList.
     */
    public List<Customer> findAllCustomers() throws SQLException {
        String sql = "SELECT user_id, name, email, phone, password_hash, role, created_at FROM users WHERE role = 'USER' ORDER BY user_id DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Customer> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Customer c = new Customer(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("password_hash"),
                        rs.getTimestamp("created_at")
                );
                list.add(c);
            }
            return list;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Helper to instantiate Customer or Admin based on role.
     */
    private Person mapRowToPerson(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String passHash = rs.getString("password_hash");
        String role = rs.getString("role");
        Timestamp createdAt = rs.getTimestamp("created_at");

        if (Person.ROLE_ADMIN.equalsIgnoreCase(role)) {
            return new Admin(id, name, email, phone, passHash, "Operations", createdAt);
        } else {
            return new Customer(id, name, email, phone, passHash, createdAt);
        }
    }
}

