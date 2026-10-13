package com.securebank.model;

import java.sql.Timestamp;

/**
 * Module I: Inheritance (Person -> Admin).
 * Represents a bank employee or administrator with elevated privileges.
 * - Demonstrates calling super(...) in constructor.
 */
public class Admin extends Person {

    private String department;

    public Admin() {
        super();
        this.role = ROLE_ADMIN;
        this.department = "Operations";
    }

    public Admin(int userId, String name, String email, String phone, String passwordHash, String department, Timestamp createdAt) {
        super(userId, name, email, phone, passwordHash, ROLE_ADMIN, createdAt);
        this.department = (department != null && !department.isEmpty()) ? department : "Operations";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + getUserId() +
                ", name='" + getName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}

