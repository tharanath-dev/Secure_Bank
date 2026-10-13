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
import java.util.Map;

/**
 * Module V: Administrator Dashboard Servlet.
 * Displays overall platform metrics and recent customer accounts.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

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

        try {
            Map<String, Integer> stats = adminService.getDashboardStatistics();
            List<Customer> allCustomers = adminService.getAllCustomersWithAccounts();

            request.setAttribute("stats", stats);
            // Display first 5 recent customers on the summary dashboard
            List<Customer> recent = allCustomers.size() > 5 ? allCustomers.subList(0, 5) : allCustomers;
            request.setAttribute("recentCustomers", recent);

            SessionUtil.recordActivity(request, "Viewed Admin Dashboard");
            request.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Error loading administrator dashboard: " + e.getMessage());
            request.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(request, response);
        }
    }
}

