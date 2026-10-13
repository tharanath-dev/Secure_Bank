package com.securebank.model;

import java.sql.Timestamp;

/**
 * Module I: Classes, Objects, Inheritance, Access Specifiers, Static & Final keywords.
 * 
 * Abstract base class representing any banking system user (Customer or Admin).
 * - Demonstrates 'abstract' non-access specifier.
 * - Demonstrates 'private', 'protected', and 'public' access specifiers.
 * - Demonstrates 'static' and 'final' constants.
 * - Demonstrates 'final' method that cannot be overridden by subclasses.
 */
public abstract class Person {

    // Module I: Static and Final constants
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Module I: Access specifiers: private fields for encapsulation
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;

    // Module I: Protected field accessible within package and subclasses
    protected String role;

    private Timestamp createdAt;

    // Default package-private constructor (Module I: default access specifier)
    Person() {
    }

    /**
     * Module I: Parameterized constructor using 'this' keyword to resolve variable shadowing.
     */
    public Person(int userId, String name, String email, String phone, String passwordHash, String role, Timestamp createdAt) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters demonstrating encapsulation
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Module I: Final method. Subclasses cannot override this security-critical check.
     */
    public final boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(this.role);
    }

    /**
     * Module I: Final helper method to mask email for security display (e.g. j***@example.com).
     */
    public final String getMaskedEmail() {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}

