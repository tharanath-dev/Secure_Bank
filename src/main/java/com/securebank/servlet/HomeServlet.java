package com.securebank.servlet;

import com.securebank.dao.TokenDAO;
import com.securebank.service.AdminService;
import com.securebank.service.TokenCleanupTask;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Module V: Servlet Lifecycle (init, doGet, destroy).
 * Serves the public landing page and initializes background tasks.
 */
@WebServlet(urlPatterns = {"", "/home"}, loadOnStartup = 1)
public class HomeServlet extends HttpServlet {

    private AdminService adminService;

    /**
     * Module V: Servlet lifecycle init() method.
     * Demonstrates Module III: Starting background daemon Thread from servlet initialization.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        this.adminService = new AdminService();
        // Module III: Background thread initiated once during servlet initialization
        TokenCleanupTask.startOnce(new TokenDAO());
        System.out.println("[HomeServlet] Initialized and started background token cleanup daemon.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            boolean setupNeeded = adminService.isSetupNeeded();
            request.setAttribute("setupNeeded", setupNeeded);
        } catch (Exception e) {
            request.setAttribute("setupNeeded", false);
        }
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    /**
     * Module V: Servlet lifecycle destroy() method.
     */
    @Override
    public void destroy() {
        TokenCleanupTask.stop();
        System.out.println("[HomeServlet] Destroyed and background tasks signaled to stop.");
        super.destroy();
    }
}

