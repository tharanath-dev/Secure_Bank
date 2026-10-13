package com.securebank.servlet;

import com.securebank.model.Account;
import com.securebank.model.Customer;
import com.securebank.model.Person;
import com.securebank.model.Transaction;
import com.securebank.service.AdminService;
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
 * Module V: Admin Customer Account & Transaction Inspector.
 * Demonstrates:
 * - Module II: Static nested class Account.AccountSummary instantiation
 */
@WebServlet("/admin/customer-details")
public class AdminCustomerDetailsServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(request, response, Person.ROLE_ADMIN)) return;

        String userIdStr = request.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=No+customer+specified.");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr.trim());
            Customer customer = adminService.getCustomerDetails(userId);
            if (customer == null) {
                response.sendRedirect(request.getContextPath() + "/admin/customers?error=Customer+not+found.");
                return;
            }

            Account account = customer.getAccount();
            List<Transaction> transactions = null;
            if (account != null) {
                transactions = adminService.getCustomerTransactions(account.getAccountId());

                // Module II: Instantiate static nested class Account.AccountSummary
                Account.AccountSummary summary = new Account.AccountSummary(
                        account.getAccountNumber(),
                        customer.getName(),
                        account.getAccountType(),
                        account.getBalance(),
                        account.getStatus()
                );
                request.setAttribute("accountSummary", summary);
            }

            request.setAttribute("customer", customer);
            request.setAttribute("account", account);
            request.setAttribute("transactions", transactions);

            SessionUtil.recordActivity(request, "Inspected Customer #" + userId);
            request.getRequestDispatcher("/jsp/admin/customer_details.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid+customer+identifier.");
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/admin/customers.jsp").forward(request, response);
        }
    }
}

