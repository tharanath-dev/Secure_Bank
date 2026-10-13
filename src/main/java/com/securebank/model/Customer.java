package com.securebank.model;

import java.sql.Timestamp;

/**
 * Module I: Inheritance (Person -> Customer).
 * Represents a standard retail banking customer.
 * - Demonstrates calling super(...) in subclass constructor.
 * - Demonstrates object composition (Customer has an Account).
 */
public class Customer extends Person {

    private Account account;

    public Customer() {
        super();
        this.role = ROLE_USER;
    }

    /**
     * Module I: Subclass constructor invoking superclass constructor via super(...)
     */
    public Customer(int userId, String name, String email, String phone, String passwordHash, Timestamp createdAt) {
        super(userId, name, email, phone, passwordHash, ROLE_USER, createdAt);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + getUserId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                '}';
    }
}

