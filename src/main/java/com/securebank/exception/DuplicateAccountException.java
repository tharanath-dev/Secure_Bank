package com.securebank.exception;

/**
 * Module III: Custom Checked Exceptions
 * Thrown when trying to register a user with an email or account number that already exists.
 */
public class DuplicateAccountException extends Exception {
    public DuplicateAccountException(String message) {
        super(message);
    }

    public DuplicateAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}

