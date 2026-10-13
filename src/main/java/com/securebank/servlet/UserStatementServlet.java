package com.securebank.servlet;

import com.securebank.model.Account;
import com.securebank.model.Customer;
import com.securebank.model.Person;
import com.securebank.model.Transaction;
import com.securebank.service.BankService;
import com.securebank.util.FileAuditLogger;
import com.securebank.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Module IV: File I/O using Character Streams.
 * Generates an official bank account statement and streams it as a downloadable .txt file.
 */
@WebServlet("/user/statement")
public class UserStatementServlet extends HttpServlet {

    private BankService bankService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.bankService = new BankService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;

        Customer customer = (Customer) SessionUtil.getLoggedUser(request);
        Account account = SessionUtil.getLoggedAccount(request);

        try {
            // Fetch updated transactions from database
            List<Transaction> transactions = bankService.getTransactions(account.getAccountId());

            // Configure HTTP response headers for text file download
            String filename = "statement_" + account.getAccountNumber() + ".txt";
            response.setContentType("text/plain; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

            // Module IV: Character stream PrintWriter wrapped over response output stream
            PrintWriter writer = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)));

            // Write formatted text statement
            writer.println("================================================================================");
            writer.println("                       SECUREBANK - ACCOUNT STATEMENT                           ");
            writer.println("================================================================================");
            writer.println("Generated Date : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            writer.println("Account Number : " + account.getAccountNumber());
            writer.println("Account Holder : " + customer.getName());
            writer.println("Email Address  : " + customer.getEmail());
            writer.println("Account Type   : " + account.getAccountType());
            writer.println("Account Status : " + account.getStatus());
            writer.println("Current Balance: " + account.getFormattedBalance());
            writer.println("================================================================================");
            writer.println();
            writer.printf("%-18s | %-12s | %-12s | %-14s | %-15s | %s%n",
                    "Transaction ID", "Date", "Type", "Amount", "Balance After", "Details / Remark");
            writer.println("--------------------------------------------------------------------------------");

            if (transactions.isEmpty()) {
                writer.println("                    No transactions recorded for this account.                  ");
            } else {
                for (Transaction t : transactions) {
                    writer.printf("%-18s | %-12s | %-12s | %-14s | %-15s | %s%n",
                            t.getTransactionId(),
                            t.getFormattedDate(),
                            t.getType(),
                            t.getFormattedAmount(),
                            t.getFormattedBalanceAfter(),
                            (t.getRemark() != null ? t.getRemark() : "-")
                    );
                }
            }

            writer.println("================================================================================");
            writer.println("           Thank you for banking with SecureBank Management System              ");
            writer.println("================================================================================");

            writer.flush();
            writer.close();

            FileAuditLogger.logAction("DOWNLOAD_STATEMENT", customer.getEmail(),
                    "Downloaded transaction statement for account " + account.getAccountNumber());

        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error generating statement: " + e.getMessage());
        }
    }
}

