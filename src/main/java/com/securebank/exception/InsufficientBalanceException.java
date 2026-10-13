package com.securebank.exception;

/**
 * Module III: Custom Checked Exceptions
 * Thrown when an account has insufficient funds to fulfill a withdrawal or transfer request.
 */
public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}

