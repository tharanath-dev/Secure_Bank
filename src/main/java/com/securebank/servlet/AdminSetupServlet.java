package com.securebank.servlet;

import com.securebank.exception.DuplicateAccountException;
import com.securebank.service.AdminService;
import com.securebank.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

/**
 * Module V: First-run Setup Servlet.
 * Operates ONLY while no administrative account exists in the database.
 */
@WebServlet("/admin/setup")
public class AdminSetupServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (!adminService.isSetupNeeded()) {
                response.sendRedirect(request.getContextPath() + "/admin/login?error=System+is+already+initialized.+Setup+is+disabled.");
                return;
            }
            request.getRequestDispatcher("/jsp/admin/setup.jsp").forward(request, response);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database check failed: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            if (!adminService.isSetupNeeded()) {
                response.sendRedirect(request.getContextPath() + "/admin/login?error=System+is+already+initialized.");
                return;
            }
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
            return;
        }

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String department = request.getParameter("department");

        if (ValidationUtil.isBlank(name) || !ValidationUtil.isValidEmail(email) || 
            !ValidationUtil.isValidPhone(phone) || !ValidationUtil.isValidPassword(password)) {
            request.setAttribute("errorMessage", "All fields are required. Password must be at least 6 characters.");
            request.setAttribute("paramName", name);
            request.setAttribute("paramEmail", email);
            request.setAttribute("paramPhone", phone);
            request.setAttribute("paramDept", department);
            request.getRequestDispatcher("/jsp/admin/setup.jsp").forward(request, response);
            return;
        }

        try {
            boolean created = adminService.setupFirstAdmin(name, email, phone, password, department);
            if (created) {
                String successMsg = "First Administrator (" + email + ") successfully created! Please log in.";
                response.sendRedirect(request.getContextPath() + "/admin/login?success=" + URLEncoder.encode(successMsg, "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/login?error=System+already+initialized.");
            }
        } catch (DuplicateAccountException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.getRequestDispatcher("/jsp/admin/setup.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/admin/setup.jsp").forward(request, response);
        }
    }
}

