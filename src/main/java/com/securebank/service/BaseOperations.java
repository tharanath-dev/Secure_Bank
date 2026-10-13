package com.securebank.service;

import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.InactiveAccountException;

/**
 * Module II: Interfaces and Interface Constants
 * 
 * Base banking operations interface demonstrating:
 * - Interface definition
 * - Interface constants (implicitly public static final)
 * - Method contracts
 */
public interface BaseOperations {

    // Module II: Interface constant
    String CURRENCY_SYMBOL = "Rs. ";
    String DEFAULT_REMARK = "General Banking Operation";

    /**
     * Checks current real-time balance for an account.
     */
    double checkBalance(String accountNumber) throws AccountNotFoundException, InactiveAccountException;
}

