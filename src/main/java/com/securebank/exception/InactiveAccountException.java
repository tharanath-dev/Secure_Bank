package com.securebank.exception;

/**
 * Module III: Custom Checked Exceptions
 * Thrown when an operation is attempted on an account whose status is INACTIVE (deactivated).
 */
public class InactiveAccountException extends Exception {
    public InactiveAccountException(String message) {
        super(message);
    }

    public InactiveAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}

