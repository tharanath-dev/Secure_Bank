package com.securebank.servlet;

import com.securebank.exception.DuplicateAccountException;
import com.securebank.exception.InvalidAmountException;
import com.securebank.model.Customer;
import com.securebank.service.UserService;
import com.securebank.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/**
 * Module V: Servlet handling customer account registration.
 * Demonstrates:
 * - Module III: Exception handling with try-catch
 * - Module IV: Error map verification
 * - Module V: Reading parameters and forwarding
 */
@WebServlet("/user/register")
public class UserRegisterServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String accountType = request.getParameter("accountType");
        String depositStr = request.getParameter("openingDeposit");

        // Module IV: Validate form inputs and obtain errors Map
        Map<String, String> errors = ValidationUtil.validateRegistration(name, email, phone, password, accountType);

        double openingDeposit = 0.0;
        if (depositStr != null && !depositStr.trim().isEmpty()) {
            Double parsed = ValidationUtil.parsePositiveAmount(depositStr);
            if (parsed == null) {
                errors.put("openingDeposit", "Opening deposit must be a valid positive amount.");
            } else {
                openingDeposit = parsed;
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            request.setAttribute("paramName", name);
            request.setAttribute("paramEmail", email);
            request.setAttribute("paramPhone", phone);
            request.setAttribute("paramAccountType", accountType);
            request.setAttribute("paramDeposit", depositStr);
            request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
            return;
        }

        try {
            Customer customer = userService.registerCustomer(name, email, phone, password, accountType, openingDeposit);
            String successMsg = "Account registration successful! Your Account Number is " +
                    customer.getAccount().getAccountNumber() + ". Please login to continue.";
            response.sendRedirect(request.getContextPath() + "/user/login?success=" + java.net.URLEncoder.encode(successMsg, "UTF-8"));
        } catch (DuplicateAccountException | InvalidAmountException e) {
            // Module III: Multi-catch exception handling
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramName", name);
            request.setAttribute("paramEmail", email);
            request.setAttribute("paramPhone", phone);
            request.setAttribute("paramAccountType", accountType);
            request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database system error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/user/register.jsp").forward(request, response);
        }
    }
}

