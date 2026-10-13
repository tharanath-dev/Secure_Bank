package com.securebank.servlet;

import com.securebank.exception.InvalidCredentialsException;
import com.securebank.model.Customer;
import com.securebank.model.Person;
import com.securebank.service.UserService;
import com.securebank.util.SessionUtil;
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
 * Module V: Customer Profile & Security Management Servlet.
 */
@WebServlet("/user/profile")
public class UserProfileServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;
        request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;

        Customer customer = (Customer) SessionUtil.getLoggedUser(request);
        String action = request.getParameter("action");

        if ("updateProfile".equalsIgnoreCase(action)) {
            String name = request.getParameter("name");
            String phone = request.getParameter("phone");

            if (ValidationUtil.isBlank(name) || name.trim().length() < 3) {
                request.setAttribute("profileError", "Name must be at least 3 characters long.");
                request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
                return;
            }

            if (!ValidationUtil.isValidPhone(phone)) {
                request.setAttribute("profileError", "Please provide a valid 10 to 15-digit phone number.");
                request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
                return;
            }

            try {
                userService.updateProfile(customer.getUserId(), name.trim(), phone.trim());
                customer.setName(name.trim());
                customer.setPhone(phone.trim());

                String msg = "Profile details updated successfully.";
                response.sendRedirect(request.getContextPath() + "/user/profile?success=" + URLEncoder.encode(msg, "UTF-8"));
            } catch (SQLException e) {
                request.setAttribute("profileError", "Error updating profile: " + e.getMessage());
                request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
            }

        } else if ("changePassword".equalsIgnoreCase(action)) {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            if (!ValidationUtil.isValidPassword(newPassword)) {
                request.setAttribute("passwordError", "New password must be at least 6 characters long.");
                request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("passwordError", "New password and confirmation do not match.");
                request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
                return;
            }

            try {
                userService.changePassword(customer.getUserId(), currentPassword, newPassword);
                String msg = "Password updated successfully.";
                response.sendRedirect(request.getContextPath() + "/user/profile?success=" + URLEncoder.encode(msg, "UTF-8"));
            } catch (InvalidCredentialsException e) {
                request.setAttribute("passwordError", e.getMessage());
                request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
            } catch (SQLException e) {
                request.setAttribute("passwordError", "Database error: " + e.getMessage());
                request.getRequestDispatcher("/jsp/user/profile.jsp").forward(request, response);
            }
        }
    }
}

