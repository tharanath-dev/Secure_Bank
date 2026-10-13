package com.securebank.servlet;

import com.securebank.model.Person;
import com.securebank.util.FileAuditLogger;
import com.securebank.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Module V: Administrator Logout Servlet.
 */
@WebServlet("/admin/logout")
public class AdminLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Person admin = SessionUtil.getLoggedUser(request);
        if (admin != null) {
            FileAuditLogger.logAction("ADMIN_LOGOUT", admin.getEmail(), "Admin signed out.");
        }
        SessionUtil.invalidateSession(request);
        response.sendRedirect(request.getContextPath() + "/admin/login?success=Administrator+logged+out+safely.");
    }
}

