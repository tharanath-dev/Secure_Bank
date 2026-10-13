package com.securebank.servlet;

import com.securebank.model.Account;
import com.securebank.model.Customer;
import com.securebank.model.Person;
import com.securebank.model.Transaction;
import com.securebank.service.BankService;
import com.securebank.service.UserService;
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
 * Module V: Customer Dashboard Servlet.
 * Enforces role authorization and prepares summary data.
 */
@WebServlet("/user/dashboard")
public class UserDashboardServlet extends HttpServlet {

    private BankService bankService;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.bankService = new BankService();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Server-side role enforcement
        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) {
            return;
        }

        Customer customer = (Customer) SessionUtil.getLoggedUser(request);
        try {
            // Fetch fresh account state from database
            Account account = userService.getAccountForUser(customer.getUserId());
            customer.setAccount(account);
            SessionUtil.updateSessionAccount(request, account);

            // Fetch last 5 transactions
            List<Transaction> recentTransactions = bankService.getRecentTransactions(account.getAccountId(), 5);

            request.setAttribute("customer", customer);
            request.setAttribute("account", account);
            request.setAttribute("recentTransactions", recentTransactions);

            SessionUtil.recordActivity(request, "Viewed Dashboard");
            request.getRequestDispatcher("/jsp/user/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Error loading account dashboard: " + e.getMessage());
            request.getRequestDispatcher("/jsp/user/dashboard.jsp").forward(request, response);
        }
    }
}

