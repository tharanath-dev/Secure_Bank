package com.securebank.servlet;

import com.securebank.exception.InvalidCredentialsException;
import com.securebank.model.Admin;
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

/**
 * Module V: Administrator Login Servlet.
 */
@WebServlet("/admin/login")
public class AdminLoginServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if admin is already logged in
        if (SessionUtil.getLoggedUser(request) != null && Person.ROLE_ADMIN.equalsIgnoreCase((String) request.getSession().getAttribute(SessionUtil.SESSION_ROLE))) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        try {
            boolean setupNeeded = adminService.isSetupNeeded();
            request.setAttribute("setupNeeded", setupNeeded);
        } catch (SQLException ignored) {
        }

        request.getRequestDispatcher("/jsp/admin/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required.");
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/jsp/admin/login.jsp").forward(request, response);
            return;
        }

        try {
            Admin admin = adminService.loginAdmin(email, password);
            SessionUtil.createSession(request, admin, null);
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } catch (InvalidCredentialsException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/jsp/admin/login.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database system error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/admin/login.jsp").forward(request, response);
        }
    }
}

