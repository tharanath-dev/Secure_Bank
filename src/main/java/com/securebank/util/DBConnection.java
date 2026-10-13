package com.securebank.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Module V: JDBC Connection Manager
 * 
 * Provides centralized database connectivity using DriverManager, PreparedStatement,
 * and ResultSet, with safe resource closure in finally blocks.
 */
public class DBConnection {

    private static String jdbcUrl;
    private static String jdbcUser;
    private static String jdbcPassword;
    private static String driverClass;

    static {
        try {
            Properties props = new Properties();
            InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            if (in != null) {
                props.load(in);
                in.close();
            }

            // Read properties with fallback defaults
            driverClass = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            jdbcUrl = props.getProperty("db.url", "jdbc:mysql://localhost:3306/securebank_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8");
            jdbcUser = props.getProperty("db.user", "root");
            jdbcPassword = props.getProperty("db.password", "root");

            // Allow environment variable overrides if present
            String envUrl = System.getenv("SECUREBANK_DB_URL");
            if (envUrl != null && !envUrl.trim().isEmpty()) {
                jdbcUrl = envUrl.trim();
            }
            String envUser = System.getenv("SECUREBANK_DB_USER");
            if (envUser != null && !envUser.trim().isEmpty()) {
                jdbcUser = envUser.trim();
            }
            String envPass = System.getenv("SECUREBANK_DB_PASS");
            if (envPass != null) {
                jdbcPassword = envPass;
            }

            Class.forName(driverClass);
        } catch (Exception e) {
            System.err.println("[DBConnection] Initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new database connection from DriverManager.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    /**
     * Module III & V: Resource Cleanup Helper
     * Closes Connection, Statement, and ResultSet safely in finally blocks.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ignored) {
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}

