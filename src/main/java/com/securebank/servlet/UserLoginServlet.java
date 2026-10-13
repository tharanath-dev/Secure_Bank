package com.securebank.servlet;

import com.securebank.exception.InactiveAccountException;
import com.securebank.exception.InvalidCredentialsException;
import com.securebank.model.Customer;
import com.securebank.service.UserService;
import com.securebank.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Module V: Servlet for customer login.
 * Demonstrates:
 * - Module III: Exception handling for bad credentials and deactivated accounts
 * - Module V: HttpSession creation and redirect
 */
@WebServlet("/user/login")
public class UserLoginServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // If already logged in as USER, go straight to dashboard
        if (SessionUtil.getLoggedUser(request) != null && "USER".equalsIgnoreCase((String) request.getSession().getAttribute(SessionUtil.SESSION_ROLE))) {
            response.sendRedirect(request.getContextPath() + "/user/dashboard");
            return;
        }
        request.getRequestDispatcher("/jsp/user/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required.");
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/jsp/user/login.jsp").forward(request, response);
            return;
        }

        try {
            Customer customer = userService.loginCustomer(email, password);
            // Module V: Establish HTTP session
            SessionUtil.createSession(request, customer, customer.getAccount());
            response.sendRedirect(request.getContextPath() + "/user/dashboard");
        } catch (InvalidCredentialsException | InactiveAccountException e) {
            // Module III: Multiple catch for specific business exceptions
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramEmail", email);
            request.getRequestDispatcher("/jsp/user/login.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database error during login: " + e.getMessage());
            request.getRequestDispatcher("/jsp/user/login.jsp").forward(request, response);
        }
    }
}

