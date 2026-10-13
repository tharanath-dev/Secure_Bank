package com.securebank.service;

import com.securebank.dao.AccountDAO;
import com.securebank.dao.TransactionDAO;
import com.securebank.dao.UserDAO;
import com.securebank.exception.DuplicateAccountException;
import com.securebank.exception.InvalidCredentialsException;
import com.securebank.model.Account;
import com.securebank.model.Admin;
import com.securebank.model.Customer;
import com.securebank.model.Person;
import com.securebank.model.Transaction;
import com.securebank.util.FileAuditLogger;
import com.securebank.util.PasswordUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service managing administrative features.
 * Demonstrates:
 * - Module IV: Lambda Expressions & Stream API for searching and aggregation
 * - Module IV: Collections (ArrayList, Map)
 * - Module I: Admin inheritance from Person
 */
public class AdminService {

    private final UserDAO userDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public AdminService() {
        this.userDAO = new UserDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    /**
     * Checks if initial system setup is required (no admin accounts exist).
     */
    public boolean isSetupNeeded() throws SQLException {
        return userDAO.countAdmins() == 0;
    }

    /**
     * First-run setup: creates the first administrative account.
     * Can only be executed if no admin exists in the database.
     */
    public synchronized boolean setupFirstAdmin(String name, String email, String phone, String plainPassword, String department)
            throws DuplicateAccountException, SQLException {

        if (!isSetupNeeded()) {
            return false; // Already initialized
        }

        Person existing = userDAO.findByEmail(email);
        if (existing != null) {
            throw new DuplicateAccountException("Email is already registered.");
        }

        Admin admin = new Admin();
        admin.setName(name.trim());
        admin.setEmail(email.trim().toLowerCase());
        admin.setPhone(phone.trim());
        admin.setPasswordHash(PasswordUtil.hashPassword(plainPassword));
        admin.setDepartment((department != null && !department.trim().isEmpty()) ? department.trim() : "System Operations");

        userDAO.create(admin);

        FileAuditLogger.logAction("ADMIN_SETUP", email, "Initial system Administrator provisioned.");
        return true;
    }

    /**
     * Authenticates administrator credentials.
     */
    public Admin loginAdmin(String email, String plainPassword) throws InvalidCredentialsException, SQLException {
        if (email == null || plainPassword == null) {
            throw new InvalidCredentialsException("Credentials cannot be empty.");
        }

        Person person = userDAO.findByEmail(email.trim().toLowerCase());
        if (person == null || !person.isAdmin()) {
            throw new InvalidCredentialsException("Invalid administrator credentials.");
        }

        if (!PasswordUtil.verifyPassword(plainPassword, person.getPasswordHash())) {
            FileAuditLogger.logAction("ADMIN_LOGIN_FAIL", email, "Failed admin login attempt.");
            throw new InvalidCredentialsException("Invalid administrator credentials.");
        }

        FileAuditLogger.logAction("ADMIN_LOGIN", email, "Admin logged into console.");
        return (Admin) person;
    }

    /**
     * Returns aggregate system metrics for Admin Dashboard.
     */
    public Map<String, Integer> getDashboardStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalCustomers", userDAO.countCustomers());
        stats.put("totalAccounts", accountDAO.countAccounts());
        stats.put("activeAccounts", accountDAO.countActiveAccounts());
        stats.put("inactiveAccounts", accountDAO.countInactiveAccounts());
        return stats;
    }

    /**
     * Module IV: Returns all registered customers with their accounts populated.
     */
    public List<Customer> getAllCustomersWithAccounts() throws SQLException {
        List<Customer> customers = userDAO.findAllCustomers();
        for (Customer c : customers) {
            Account acc = accountDAO.findByUserId(c.getUserId());
            c.setAccount(acc);
        }
        return customers;
    }

    /**
     * Module IV: Streams and Lambdas - Search customers by name or account number.
     */
    public List<Customer> searchCustomers(String query) throws SQLException {
        List<Customer> all = getAllCustomersWithAccounts();
        if (query == null || query.trim().isEmpty()) {
            return all;
        }

        final String q = query.trim().toLowerCase();
        // Module IV: Stream filter with Lambda expression
        return all.stream()
                .filter(c -> {
                    boolean nameMatch = c.getName() != null && c.getName().toLowerCase().contains(q);
                    boolean emailMatch = c.getEmail() != null && c.getEmail().toLowerCase().contains(q);
                    boolean accMatch = c.getAccount() != null && c.getAccount().getAccountNumber().contains(q);
                    return nameMatch || emailMatch || accMatch;
                })
                .collect(Collectors.toList());
    }

    /**
     * Module IV: Streams and Lambdas - Filters transaction list by type.
     */
    public List<Transaction> filterTransactionsByType(List<Transaction> transactions, String type) {
        if (type == null || type.trim().isEmpty() || "ALL".equalsIgnoreCase(type)) {
            return transactions;
        }
        return transactions.stream()
                .filter(t -> type.equalsIgnoreCase(t.getType()))
                .collect(Collectors.toList());
    }

    /**
     * Module IV: Streams and Lambdas - Calculates total deposit volume.
     */
    public double calculateTotalDeposits(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> Transaction.TYPE_DEPOSIT.equalsIgnoreCase(t.getType()) || Transaction.TYPE_TRANSFER_IN.equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Module IV: Streams and Lambdas - Calculates total withdrawal volume.
     */
    public double calculateTotalWithdrawals(List<Transaction> transactions) {
        return transactions.stream()
                .filter(t -> Transaction.TYPE_WITHDRAW.equalsIgnoreCase(t.getType()) || Transaction.TYPE_TRANSFER_OUT.equalsIgnoreCase(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Toggles an account's status between ACTIVE and INACTIVE.
     */
    public boolean toggleAccountStatus(int accountId) throws SQLException {
        Account account = accountDAO.findById(accountId);
        if (account == null) {
            return false;
        }

        String newStatus = account.isActive() ? Account.STATUS_INACTIVE : Account.STATUS_ACTIVE;
        boolean ok = accountDAO.updateStatus(accountId, newStatus);
        if (ok) {
            FileAuditLogger.logAction("STATUS_TOGGLE", account.getAccountNumber(), "Status changed to " + newStatus);
        }
        return ok;
    }

    public Customer getCustomerDetails(int userId) throws SQLException {
        Person p = userDAO.findById(userId);
        if (p instanceof Customer) {
            Customer c = (Customer) p;
            c.setAccount(accountDAO.findByUserId(userId));
            return c;
        }
        return null;
    }

    public List<Transaction> getCustomerTransactions(int accountId) throws SQLException {
        return transactionDAO.findByAccountId(accountId);
    }
}

