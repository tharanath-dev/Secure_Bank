package com.securebank.util;

import com.securebank.model.Account;
import com.securebank.model.Person;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Stack;

/**
 * Utility for session lifecycle management, authentication, and role authorization.
 * Demonstrates:
 * - Module V: HttpSession management, request redirection.
 * - Module IV: java.util.Stack for tracking user navigation history.
 */
public class SessionUtil {

    public static final String SESSION_USER = "loggedUser";
    public static final String SESSION_ACCOUNT = "loggedAccount";
    public static final String SESSION_ROLE = "userRole";
    public static final String SESSION_HISTORY_STACK = "navHistoryStack";

    /**
     * Stores user authentication details in HTTP session.
     */
    public static void createSession(HttpServletRequest request, Person person, Account account) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_USER, person);
        session.setAttribute(SESSION_ROLE, person.getRole());
        if (account != null) {
            session.setAttribute(SESSION_ACCOUNT, account);
        }

        // Module IV: Stack collection for recently viewed actions
        Stack<String> historyStack = new Stack<>();
        historyStack.push("Login at " + new java.util.Date());
        session.setAttribute(SESSION_HISTORY_STACK, historyStack);
    }

    /**
     * Module IV: Pushes an action to the user session navigation Stack.
     */
    @SuppressWarnings("unchecked")
    public static void recordActivity(HttpServletRequest request, String activity) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Stack<String> stack = (Stack<String>) session.getAttribute(SESSION_HISTORY_STACK);
            if (stack == null) {
                stack = new Stack<>();
                session.setAttribute(SESSION_HISTORY_STACK, stack);
            }
            stack.push(activity);
            // Limit stack to recent 10 items
            if (stack.size() > 10) {
                stack.remove(0);
            }
        }
    }

    /**
     * Retrieves current logged in user from session.
     */
    public static Person getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Person) session.getAttribute(SESSION_USER);
        }
        return null;
    }

    /**
     * Retrieves current user's bank account from session.
     */
    public static Account getLoggedAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Account) session.getAttribute(SESSION_ACCOUNT);
        }
        return null;
    }

    /**
     * Updates account in session after transactions.
     */
    public static void updateSessionAccount(HttpServletRequest request, Account account) {
        HttpSession session = request.getSession(false);
        if (session != null && account != null) {
            session.setAttribute(SESSION_ACCOUNT, account);
        }
    }

    /**
     * Enforces role-based server-side access control.
     * Returns true if authorized; returns false and redirects to login if unauthorized.
     */
    public static boolean requireRole(HttpServletRequest request, HttpServletResponse response, String expectedRole)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            redirectToLogin(request, response, expectedRole);
            return false;
        }

        Person user = (Person) session.getAttribute(SESSION_USER);
        String role = (String) session.getAttribute(SESSION_ROLE);

        if (user == null || role == null || !role.equalsIgnoreCase(expectedRole)) {
            redirectToLogin(request, response, expectedRole);
            return false;
        }

        return true;
    }

    /**
     * Redirects to the appropriate login page based on expected role.
     */
    private static void redirectToLogin(HttpServletRequest request, HttpServletResponse response, String expectedRole)
            throws IOException {
        String contextPath = request.getContextPath();
        if (Person.ROLE_ADMIN.equalsIgnoreCase(expectedRole)) {
            response.sendRedirect(contextPath + "/admin/login?error=Access+denied.+Please+login+as+Administrator.");
        } else {
            response.sendRedirect(contextPath + "/user/login?error=Session+expired+or+login+required.");
        }
    }

    /**
     * Invalidates the active HTTP session.
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}

