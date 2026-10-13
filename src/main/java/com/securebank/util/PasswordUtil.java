package com.securebank.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Utility for cryptographic hashing and secure random token generation.
 * Uses java.security.MessageDigest and java.security.SecureRandom.
 */
public class PasswordUtil {

    // Fixed salt string to strengthen SHA-256 against simple rainbow tables
    private static final String STATIC_SALT = "SecureBank#Salt_2026!Key";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Hashes a plain-text password using salted SHA-256.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        return hashWithSalt(plainPassword, STATIC_SALT);
    }

    /**
     * Verifies whether a candidate plain password matches a stored SHA-256 hash.
     */
    public static boolean verifyPassword(String candidatePassword, String storedHash) {
        if (candidatePassword == null || storedHash == null) {
            return false;
        }
        String candidateHash = hashPassword(candidatePassword);
        return candidateHash.equalsIgnoreCase(storedHash);
    }

    /**
     * Computes raw SHA-256 hash for reset tokens or identifiers.
     */
    public static String hashToken(String token) {
        if (token == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available in runtime", e);
        }
    }

    /**
     * Internal helper to hash text with salt.
     */
    private static String hashWithSalt(String input, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String combined = salt + input + salt;
            byte[] bytes = md.digest(combined.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Converts a byte array to hex string.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Generates a cryptographically strong random token for password resets.
     */
    public static String generateRandomToken() {
        byte[] randomBytes = new byte[24];
        RANDOM.nextBytes(randomBytes);
        return bytesToHex(randomBytes);
    }

    /**
     * Generates a unique 12-digit numeric bank account number starting with '100'.
     */
    public static String generateAccountNumber() {
        long number = 100000000000L + (long) (RANDOM.nextDouble() * 899999999999L);
        return String.valueOf(number);
    }

    /**
     * Generates a unique transaction identifier formatted as "TXN" + timestamp + 4 random digits.
     */
    public static String generateTransactionId() {
        long time = System.currentTimeMillis();
        int rand = 1000 + RANDOM.nextInt(9000);
        return "TXN" + time + rand;
    }
}

