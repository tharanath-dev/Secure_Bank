package com.securebank.exception;

/**
 * Module III: Custom Checked Exceptions
 * Thrown when an authentication attempt fails due to incorrect email or password.
 */
public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}

