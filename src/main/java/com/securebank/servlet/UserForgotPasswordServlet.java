package com.securebank.servlet;

import com.securebank.exception.AccountNotFoundException;
import com.securebank.service.UserService;
import com.securebank.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Module V: Password Recovery Initiation Servlet.
 * Generates single-use secure reset token with 15-minute validity window.
 */
@WebServlet("/user/forgot-password")
public class UserForgotPasswordServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/user/forgot_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        if (!ValidationUtil.isValidEmail(email)) {
            request.setAttribute("errorMessage", "Please provide a valid registered email address.");
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/jsp/user/forgot_password.jsp").forward(request, response);
            return;
        }

        try {
            String plainToken = userService.createPasswordResetToken(email.trim());

            // Build demo reset link for display
            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String contextPath = request.getContextPath();
            String resetUrl = scheme + "://" + serverName + ":" + serverPort + contextPath + "/user/reset-password?token=" + plainToken;

            request.setAttribute("demoResetUrl", resetUrl);
            request.setAttribute("plainToken", plainToken);
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/jsp/user/forgot_password.jsp").forward(request, response);

        } catch (AccountNotFoundException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/jsp/user/forgot_password.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/user/forgot_password.jsp").forward(request, response);
        }
    }
}

