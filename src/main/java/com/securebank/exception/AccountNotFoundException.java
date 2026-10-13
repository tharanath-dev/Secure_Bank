package com.securebank.exception;

/**
 * Module III: Custom Checked Exceptions
 * Thrown when a queried account number does not exist in the bank database.
 */
public class AccountNotFoundException extends Exception {
    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

