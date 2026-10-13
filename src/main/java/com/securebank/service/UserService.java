package com.securebank.service;

import com.securebank.dao.AccountDAO;
import com.securebank.dao.TokenDAO;
import com.securebank.dao.TransactionDAO;
import com.securebank.dao.UserDAO;
import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.DuplicateAccountException;
import com.securebank.exception.InactiveAccountException;
import com.securebank.exception.InvalidAmountException;
import com.securebank.exception.InvalidCredentialsException;
import com.securebank.model.Account;
import com.securebank.model.CurrentAccount;
import com.securebank.model.Customer;
import com.securebank.model.PasswordResetToken;
import com.securebank.model.Person;
import com.securebank.model.SavingsAccount;
import com.securebank.model.Transaction;
import com.securebank.util.FileAuditLogger;
import com.securebank.util.PasswordUtil;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Service handling customer authentication, registration, profile, and password reset.
 * Demonstrates:
 * - Module I & II: Working with Person/Customer/Account polymorphic hierarchies
 * - Module III: Throwing custom checked exceptions
 * - Module IV: FileAuditLogger character stream logging
 */
public class UserService {

    private final UserDAO userDAO;
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final TokenDAO tokenDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.tokenDAO = new TokenDAO();
    }

    /**
     * Registers a new customer and automatically provisions a bank account.
     */
    public synchronized Customer registerCustomer(String name, String email, String phone, 
                                                   String plainPassword, String accountType, 
                                                   double openingDeposit) 
            throws DuplicateAccountException, InvalidAmountException, SQLException {

        if (openingDeposit < 0) {
            throw new InvalidAmountException("Opening deposit cannot be negative.");
        }

        // Check if email already registered
        Person existing = userDAO.findByEmail(email);
        if (existing != null) {
            throw new DuplicateAccountException("An account with email " + email + " already exists.");
        }

        // 1. Hash password with salted SHA-256
        String passwordHash = PasswordUtil.hashPassword(plainPassword);

        // 2. Create customer entity
        Customer customer = new Customer();
        customer.setName(name.trim());
        customer.setEmail(email.trim().toLowerCase());
        customer.setPhone(phone.trim());
        customer.setPasswordHash(passwordHash);

        int userId = userDAO.create(customer);
        customer.setUserId(userId);

        // 3. Generate 12-digit unique account number
        String accountNumber;
        do {
            accountNumber = PasswordUtil.generateAccountNumber();
        } while (accountDAO.findByAccountNumber(accountNumber) != null);

        // 4. Create concrete polymorphic account
        Account account;
        if ("CURRENT".equalsIgnoreCase(accountType)) {
            account = new CurrentAccount();
        } else {
            account = new SavingsAccount();
        }

        account.setUserId(userId);
        account.setAccountNumber(accountNumber);
        account.setBalance(openingDeposit);
        account.setStatus(Account.STATUS_ACTIVE);

        int accountId = accountDAO.create(account);
        account.setAccountId(accountId);
        customer.setAccount(account);

        // 5. If initial deposit provided, log initial transaction
        if (openingDeposit > 0) {
            Transaction initialTxn = new Transaction(
                    PasswordUtil.generateTransactionId(),
                    accountId,
                    Transaction.TYPE_DEPOSIT,
                    openingDeposit,
                    null,
                    "Initial Opening Deposit",
                    openingDeposit,
                    new Timestamp(System.currentTimeMillis())
            );
            transactionDAO.insert(initialTxn);
        }

        FileAuditLogger.logAction("REGISTRATION", customer.getEmail(),
                "New " + accountType + " account created: " + accountNumber + " with opening balance Rs. " + openingDeposit);

        return customer;
    }

    /**
     * Authenticates customer credentials and verifies active account status.
     */
    public Customer loginCustomer(String email, String plainPassword)
            throws InvalidCredentialsException, InactiveAccountException, SQLException {

        if (email == null || plainPassword == null) {
            throw new InvalidCredentialsException("Email and password cannot be empty.");
        }

        Person person = userDAO.findByEmail(email.trim().toLowerCase());
        if (person == null || !Person.ROLE_USER.equalsIgnoreCase(person.getRole())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        boolean valid = PasswordUtil.verifyPassword(plainPassword, person.getPasswordHash());
        if (!valid) {
            FileAuditLogger.logAction("LOGIN_FAILED", email, "Failed login attempt (bad password).");
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        Customer customer = (Customer) person;
        Account account = accountDAO.findByUserId(customer.getUserId());
        customer.setAccount(account);

        // Requirement: Deactivated accounts cannot log in to do transactions
        if (account != null && !account.isActive()) {
            FileAuditLogger.logAction("LOGIN_BLOCKED", email, "Login blocked: Account is deactivated.");
            throw new InactiveAccountException("Your bank account (" + account.getAccountNumber() +
                    ") is currently DEACTIVATED. Please contact customer support.");
        }

        FileAuditLogger.logAction("LOGIN_SUCCESS", email, "Customer login successful.");
        return customer;
    }

    /**
     * Updates customer name and phone.
     */
    public boolean updateProfile(int userId, String name, String phone) throws SQLException {
        boolean ok = userDAO.updateProfile(userId, name, phone);
        if (ok) {
            FileAuditLogger.logAction("PROFILE_UPDATE", "User#" + userId, "Updated name=" + name + ", phone=" + phone);
        }
        return ok;
    }

    /**
     * Changes customer password after verifying old password.
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword)
            throws InvalidCredentialsException, SQLException {

        Person person = userDAO.findById(userId);
        if (person == null) {
            throw new InvalidCredentialsException("User not found.");
        }

        if (!PasswordUtil.verifyPassword(oldPassword, person.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password does not match our records.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        boolean ok = userDAO.updatePassword(userId, newHash);
        if (ok) {
            FileAuditLogger.logAction("PASSWORD_CHANGE", person.getEmail(), "Password changed successfully.");
        }
        return ok;
    }

    /**
     * Initiates password recovery by generating a random token, storing only its hash with 15-minute expiry.
     * Returns the plain token for on-screen demonstration.
     */
    public String createPasswordResetToken(String email) throws AccountNotFoundException, SQLException {
        Person person = userDAO.findByEmail(email);
        if (person == null) {
            throw new AccountNotFoundException("No user found with email address: " + email);
        }

        String plainToken = PasswordUtil.generateRandomToken();
        String tokenHash = PasswordUtil.hashToken(plainToken);

        // 15 minutes expiry from now
        Timestamp expiry = new Timestamp(System.currentTimeMillis() + (15 * 60 * 1000));
        tokenDAO.saveToken(person.getUserId(), tokenHash, expiry);

        FileAuditLogger.logAction("FORGOT_PASSWORD", email, "Generated 15-minute reset token.");
        return plainToken;
    }

    /**
     * Resets password using the plain reset token.
     */
    public boolean resetPasswordWithToken(String plainToken, String newPassword)
            throws InvalidCredentialsException, SQLException {

        String tokenHash = PasswordUtil.hashToken(plainToken);
        PasswordResetToken token = tokenDAO.findByHash(tokenHash);

        if (token == null || token.isExpired() || token.isUsed()) {
            throw new InvalidCredentialsException("Reset token is invalid, expired, or has already been used.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        userDAO.updatePassword(token.getUserId(), newHash);
        tokenDAO.markUsed(token.getTokenId());

        FileAuditLogger.logAction("PASSWORD_RESET", "User#" + token.getUserId(), "Password successfully reset via token.");
        return true;
    }

    public Account getAccountForUser(int userId) throws SQLException {
        return accountDAO.findByUserId(userId);
    }
}

