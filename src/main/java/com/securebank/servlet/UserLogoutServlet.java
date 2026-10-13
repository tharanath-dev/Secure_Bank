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
 * Module V: User Logout Servlet.
 * Invalidates user session and redirects to login screen.
 */
@WebServlet("/user/logout")
public class UserLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Person user = SessionUtil.getLoggedUser(request);
        if (user != null) {
            FileAuditLogger.logAction("LOGOUT", user.getEmail(), "Customer logged out.");
        }
        SessionUtil.invalidateSession(request);
        response.sendRedirect(request.getContextPath() + "/user/login?success=You+have+been+safely+logged+out.");
    }
}

