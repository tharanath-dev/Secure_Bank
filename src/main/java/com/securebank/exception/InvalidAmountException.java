package com.securebank.exception;

/**
 * Module III: Custom Checked Exceptions
 * Thrown when an input transaction amount is negative, zero, or violates numerical bounds.
 */
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }

    public InvalidAmountException(String message, Throwable cause) {
        super(message, cause);
    }
}

