package com.securebank.servlet;

import com.securebank.exception.DuplicateAccountException;
import com.securebank.exception.InvalidAmountException;
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
import java.util.Map;

/**
 * Module V: Admin Servlet to manually register a new customer account.
 */
@WebServlet("/admin/customers/add")
public class AdminAddCustomerServlet extends HttpServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Person.ROLE_ADMIN)) return;
        request.getRequestDispatcher("/jsp/admin/add_customer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(request, response, Person.ROLE_ADMIN)) return;

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String accountType = request.getParameter("accountType");
        String depositStr = request.getParameter("openingDeposit");

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
            request.getRequestDispatcher("/jsp/admin/add_customer.jsp").forward(request, response);
            return;
        }

        try {
            Customer customer = userService.registerCustomer(name, email, phone, password, accountType, openingDeposit);
            String msg = "Customer " + customer.getName() + " enrolled successfully with Account #" +
                    customer.getAccount().getAccountNumber();
            response.sendRedirect(request.getContextPath() + "/admin/customers?success=" + URLEncoder.encode(msg, "UTF-8"));
        } catch (DuplicateAccountException | InvalidAmountException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramName", name);
            request.setAttribute("paramEmail", email);
            request.setAttribute("paramPhone", phone);
            request.setAttribute("paramAccountType", accountType);
            request.getRequestDispatcher("/jsp/admin/add_customer.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Database system error: " + e.getMessage());
            request.getRequestDispatcher("/jsp/admin/add_customer.jsp").forward(request, response);
        }
    }
}

