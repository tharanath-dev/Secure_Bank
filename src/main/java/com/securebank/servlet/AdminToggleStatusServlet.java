package com.securebank.servlet;

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
 * Module V: Admin Servlet to toggle an account status (ACTIVE <-> INACTIVE).
 */
@WebServlet("/admin/toggle-status")
public class AdminToggleStatusServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!SessionUtil.requireRole(request, response, Person.ROLE_ADMIN)) return;

        String accountIdStr = request.getParameter("accountId");
        String returnUserId = request.getParameter("userId");

        if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid+account+ID.");
            return;
        }

        try {
            int accountId = Integer.parseInt(accountIdStr.trim());
            boolean toggled = adminService.toggleAccountStatus(accountId);

            String redirectUrl = (returnUserId != null && !returnUserId.trim().isEmpty())
                    ? request.getContextPath() + "/admin/customer-details?userId=" + returnUserId
                    : request.getContextPath() + "/admin/customers";

            if (toggled) {
                response.sendRedirect(redirectUrl + "?success=Account+status+has+been+successfully+updated.");
            } else {
                response.sendRedirect(redirectUrl + "?error=Could+not+update+account+status.");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=Invalid+account+format.");
        } catch (SQLException e) {
            response.sendRedirect(request.getContextPath() + "/admin/customers?error=Database+error:+" + e.getMessage());
        }
    }
}

