package com.securebank.servlet;

import com.securebank.model.Account;
import com.securebank.model.Person;
import com.securebank.model.Transaction;
import com.securebank.service.AdminService;
import com.securebank.service.BankService;
import com.securebank.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Module V: Customer Transaction History Servlet.
 * Demonstrates:
 * - Module IV: Using Streams & Lambdas for filtering transactions by type
 * - Module IV: Aggregating transaction volumes with stream mapToDouble and sum
 */
@WebServlet("/user/transactions")
public class UserTransactionsServlet extends HttpServlet {

    private BankService bankService;
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.bankService = new BankService();
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;

        Account account = SessionUtil.getLoggedAccount(request);
        String filterType = request.getParameter("type");
        if (filterType == null || filterType.trim().isEmpty()) {
            filterType = "ALL";
        }

        try {
            // Fetch all transactions for this account
            List<Transaction> allTransactions = bankService.getTransactions(account.getAccountId());

            // Module IV: Stream filtering via lambda
            List<Transaction> filtered = adminService.filterTransactionsByType(allTransactions, filterType);

            // Module IV: Stream mapToDouble and sum for totals
            double totalDeposits = adminService.calculateTotalDeposits(filtered);
            double totalWithdrawals = adminService.calculateTotalWithdrawals(filtered);

            request.setAttribute("transactions", filtered);
            request.setAttribute("selectedType", filterType);
            request.setAttribute("totalDeposits", totalDeposits);
            request.setAttribute("totalWithdrawals", totalWithdrawals);
            request.setAttribute("account", account);

            SessionUtil.recordActivity(request, "Viewed Transactions [" + filterType + "]");
            request.getRequestDispatcher("/jsp/user/transactions.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Unable to load transaction records: " + e.getMessage());
            request.getRequestDispatcher("/jsp/user/transactions.jsp").forward(request, response);
        }
    }
}

