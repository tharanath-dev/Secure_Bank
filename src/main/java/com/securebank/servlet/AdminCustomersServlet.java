package com.securebank.servlet;

import com.securebank.model.Customer;
import com.securebank.model.Person;
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
 * Module V: Admin Customers Listing & Search Servlet.
 * Demonstrates:
 * - Module IV: Lambda stream search across customer names, emails, and account numbers
 */
@WebServlet("/admin/customers")
public class AdminCustomersServlet extends HttpServlet {

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

        String query = request.getParameter("q");

        try {
            // Module IV: Stream and lambda search
            List<Customer> customerList = adminService.searchCustomers(query);

            request.setAttribute("customers", customerList);
            request.setAttribute("searchQuery", query);

            SessionUtil.recordActivity(request, "Searched Customers: " + (query != null ? query : "ALL"));
            request.getRequestDispatcher("/jsp/admin/customers.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Error fetching customer directory: " + e.getMessage());
            request.getRequestDispatcher("/jsp/admin/customers.jsp").forward(request, response);
        }
    }
}

