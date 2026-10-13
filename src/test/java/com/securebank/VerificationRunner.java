package com.securebank;

import com.securebank.model.Account;
import com.securebank.model.Customer;
import com.securebank.model.CurrentAccount;
import com.securebank.model.SavingsAccount;
import com.securebank.model.Transaction;
import com.securebank.service.AdminService;
import com.securebank.util.FileAuditLogger;
import com.securebank.util.PasswordUtil;
import com.securebank.util.ValidationUtil;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Verification test runner validating core banking rules, polymorphism,
 * stream filtering, character stream audit logging, and security.
 */
public class VerificationRunner {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Starting SecureBank System Verification Checks...");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // Test 1: Polymorphism & Minimum Balance (SavingsAccount)
        try {
            SavingsAccount sa = new SavingsAccount(1, 101, "100111222333", 5000.0, Account.STATUS_ACTIVE, new Timestamp(System.currentTimeMillis()));
            assertCondition(sa.getMinimumBalance() == 1000.0, "Savings minimum balance must be 1000.00");
            assertCondition(!sa.canWithdraw(4500.0), "Withdraw 4500 from 5000 must fail (violates 1000 min balance)");
            assertCondition(sa.canWithdraw(3500.0), "Withdraw 3500 from 5000 must succeed (leaves 1500 >= 1000)");
            passed++;
            System.out.println("[PASS] Test 1: SavingsAccount Polymorphism & Min Balance Rules");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 1: " + e.getMessage());
        }

        // Test 2: Polymorphism & Minimum Balance (CurrentAccount)
        try {
            CurrentAccount ca = new CurrentAccount(2, 102, "100444555666", 6000.0, Account.STATUS_ACTIVE, new Timestamp(System.currentTimeMillis()));
            assertCondition(ca.getMinimumBalance() == 5000.0, "Current minimum balance must be 5000.00");
            assertCondition(!ca.canWithdraw(2000.0), "Withdraw 2000 from 6000 must fail (violates 5000 min balance)");
            assertCondition(ca.canWithdraw(500.0), "Withdraw 500 from 6000 must succeed (leaves 5500 >= 5000)");
            passed++;
            System.out.println("[PASS] Test 2: CurrentAccount Polymorphism & Min Balance Rules");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 2: " + e.getMessage());
        }

        // Test 3: Deactivated Account Rule
        try {
            SavingsAccount inactive = new SavingsAccount(3, 103, "100777888999", 10000.0, Account.STATUS_INACTIVE, new Timestamp(System.currentTimeMillis()));
            assertCondition(!inactive.canWithdraw(500.0), "Deactivated account must NOT be allowed to withdraw");
            passed++;
            System.out.println("[PASS] Test 3: Deactivated Account Blocking Rule");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 3: " + e.getMessage());
        }

        // Test 4: Method Overloading
        try {
            SavingsAccount sa = new SavingsAccount(4, 104, "100123456789", 2000.0, Account.STATUS_ACTIVE, new Timestamp(System.currentTimeMillis()));
            sa.deposit(500.0);
            assertCondition(sa.getBalance() == 2500.0, "deposit(double) should increment balance by 500");
            sa.deposit(1000.0, "Bonus credit");
            assertCondition(sa.getBalance() == 3500.0, "deposit(double, String) should increment balance by 1000");
            passed++;
            System.out.println("[PASS] Test 4: Method Overloading deposit(double) & deposit(double, String)");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 4: " + e.getMessage());
        }

        // Test 5: Inner Class & Static Nested Class
        try {
            SavingsAccount sa = new SavingsAccount(5, 105, "100999888777", 4000.0, Account.STATUS_ACTIVE, new Timestamp(System.currentTimeMillis()));
            Account.Receipt receipt = sa.new Receipt("TXN9999", "DEPOSIT", 1000.0, new Timestamp(System.currentTimeMillis()), 5000.0);
            assertCondition("100999888777".equals(receipt.getEnclosingAccountNumber()), "Inner class should access enclosing account number");
            assertCondition(receipt.getFormattedDetails().contains("TXN9999"), "Inner class receipt should format details");

            Account.AccountSummary summary = new Account.AccountSummary("100999888777", "Jane Doe", "SAVINGS", 4000.0, "ACTIVE");
            assertCondition(summary.getFormattedBalance().contains("4,000.00"), "Static nested class should format balance");
            passed++;
            System.out.println("[PASS] Test 5: Inner Class (Receipt) & Static Nested Class (AccountSummary)");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 5: " + e.getMessage());
        }

        // Test 6: Password Hashing (Salted SHA-256)
        try {
            String plain = "UserSecretPass#2026";
            String hash = PasswordUtil.hashPassword(plain);
            assertCondition(hash != null && hash.length() == 64, "SHA-256 hash must be 64 hex characters");
            assertCondition(PasswordUtil.verifyPassword(plain, hash), "Password verification with correct password must return true");
            assertCondition(!PasswordUtil.verifyPassword("WrongPassword", hash), "Password verification with incorrect password must return false");
            passed++;
            System.out.println("[PASS] Test 6: Salted SHA-256 Password Hashing & Verification");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 6: " + e.getMessage());
        }

        // Test 7: Form & Field Validation
        try {
            Map<String, String> errors = ValidationUtil.validateRegistration("", "bademail", "123", "short", "INVALID_TYPE");
            assertCondition(errors.containsKey("name"), "Should report invalid name");
            assertCondition(errors.containsKey("email"), "Should report invalid email");
            assertCondition(errors.containsKey("phone"), "Should report invalid phone");
            assertCondition(errors.containsKey("password"), "Should report invalid password");
            assertCondition(errors.containsKey("accountType"), "Should report invalid account type");
            passed++;
            System.out.println("[PASS] Test 7: Form Validation & Error Mapping");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 7: " + e.getMessage());
        }

        // Test 8: Streams and Lambda Expressions (Filtering and Aggregating)
        try {
            AdminService as = new AdminService();
            List<Transaction> txns = new ArrayList<>();
            txns.add(new Transaction("TXN1", 1, Transaction.TYPE_DEPOSIT, 1000.0, null, "Dep 1", 1000.0, new Timestamp(System.currentTimeMillis())));
            txns.add(new Transaction("TXN2", 1, Transaction.TYPE_WITHDRAW, 400.0, null, "Wit 1", 600.0, new Timestamp(System.currentTimeMillis())));
            txns.add(new Transaction("TXN3", 1, Transaction.TYPE_TRANSFER_IN, 500.0, "1002", "Trf In", 1100.0, new Timestamp(System.currentTimeMillis())));
            txns.add(new Transaction("TXN4", 1, Transaction.TYPE_TRANSFER_OUT, 300.0, "1003", "Trf Out", 800.0, new Timestamp(System.currentTimeMillis())));

            List<Transaction> depositsOnly = as.filterTransactionsByType(txns, Transaction.TYPE_DEPOSIT);
            assertCondition(depositsOnly.size() == 1, "Filtered deposits should contain 1 element");

            double totalDeposits = as.calculateTotalDeposits(txns); // DEPOSIT (1000) + TRANSFER_IN (500) = 1500
            assertCondition(totalDeposits == 1500.0, "Total deposits should be 1500.00");

            double totalWithdrawals = as.calculateTotalWithdrawals(txns); // WITHDRAW (400) + TRANSFER_OUT (300) = 700
            assertCondition(totalWithdrawals == 700.0, "Total withdrawals should be 700.00");

            passed++;
            System.out.println("[PASS] Test 8: Lambda Expressions & Stream API (Filter, mapToDouble, sum)");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 8: " + e.getMessage());
        }

        // Test 9: File I/O Audit Logging (Character Streams)
        try {
            FileAuditLogger.logAction("TEST_RUN", "SYSTEM", "Automated system test validation entry.");
            File logFile = new File("bank_logs", "bank_audit.log");
            assertCondition(logFile.exists() && logFile.length() > 0, "Audit log file must exist and contain entries");
            passed++;
            System.out.println("[PASS] Test 9: File I/O Audit Logging with Character Streams (File: " + logFile.getPath() + ")");
        } catch (Exception e) {
            failed++;
            System.err.println("[FAIL] Test 9: " + e.getMessage());
        }

        System.out.println("==================================================");
        System.out.printf("Verification Finished: %d Passed, %d Failed%n", passed, failed);
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion Failed: " + message);
        }
    }
}

