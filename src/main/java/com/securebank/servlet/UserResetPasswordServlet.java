package com.securebank.servlet;

import com.securebank.exception.InvalidCredentialsException;
import com.securebank.service.UserService;
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
 * Module V: Password Reset Completion Servlet.
 * Verifies token hash, updates credentials, and invalidates token.
 */
@WebServlet("/user/reset-password")
public class UserResetPasswordServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (ValidationUtil.isBlank(token)) {
            request.setAttribute("errorMessage", "Missing password reset token in link.");
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher("/jsp/user/reset_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (ValidationUtil.isBlank(token)) {
            request.setAttribute("errorMessage", "Invalid or missing reset token.");
            request.getRequestDispatcher("/jsp/user/reset_password.jsp").forward(request, response);
            return;
        }

        if (!ValidationUtil.isValidPassword(newPassword)) {
            request.setAttribute("errorMessage", "New password must be at least 6 characters long.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/user/reset_password.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "New password and confirm password do not match.");
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/user/reset_password.jsp").forward(request, response);
            return;
        }

        try {
            userService.resetPasswordWithToken(token, newPassword);
            String successMsg = "Your password has been successfully reset! Please login with your new password.";
            response.sendRedirect(request.getContextPath() + "/user/login?success=" + URLEncoder.encode(successMsg, "UTF-8"));
        } catch (InvalidCredentialsException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/user/reset_password.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "System database error: " + e.getMessage());
            request.setAttribute("token", token);
            request.getRequestDispatcher("/jsp/user/reset_password.jsp").forward(request, response);
        }
    }
}

