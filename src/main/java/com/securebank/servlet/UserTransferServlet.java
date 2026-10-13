package com.securebank.servlet;

import com.securebank.exception.AccountNotFoundException;
import com.securebank.exception.InactiveAccountException;
import com.securebank.exception.InsufficientBalanceException;
import com.securebank.exception.InvalidAmountException;
import com.securebank.model.Account;
import com.securebank.model.Person;
import com.securebank.service.BankService;
import com.securebank.util.SessionUtil;
import com.securebank.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Module V: Servlet handling customer peer-to-peer funds transfer.
 * Demonstrates:
 * - Module III: Robust exception handling across business errors
 * - Module V: Transactional atomic transfer via service layer
 */
@WebServlet("/user/transfer")
public class UserTransferServlet extends HttpServlet {

    private BankService bankService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.bankService = new BankService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;
        request.getRequestDispatcher("/jsp/user/transfer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SessionUtil.requireRole(request, response, Person.ROLE_USER)) return;

        Account senderAccount = SessionUtil.getLoggedAccount(request);
        String toAccountNumber = request.getParameter("toAccountNumber");
        String amountStr = request.getParameter("amount");
        String remark = request.getParameter("remark");

        if (toAccountNumber == null || toAccountNumber.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Beneficiary account number is required.");
            request.setAttribute("paramAmount", amountStr);
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/transfer.jsp").forward(request, response);
            return;
        }

        toAccountNumber = toAccountNumber.trim();
        if (toAccountNumber.equalsIgnoreCase(senderAccount.getAccountNumber())) {
            request.setAttribute("errorMessage", "You cannot transfer funds to your own account.");
            request.setAttribute("paramAmount", amountStr);
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/transfer.jsp").forward(request, response);
            return;
        }

        Double amount = ValidationUtil.parsePositiveAmount(amountStr);
        if (amount == null) {
            request.setAttribute("errorMessage", "Please enter a valid positive numeric transfer amount.");
            request.setAttribute("paramToAccount", toAccountNumber);
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/transfer.jsp").forward(request, response);
            return;
        }

        try {
            // Execute atomic funds transfer in bankService
            bankService.transfer(senderAccount.getAccountNumber(), toAccountNumber, amount, remark);

            // Update session account
            Account updated = bankService.getValidAccount(senderAccount.getAccountNumber());
            SessionUtil.updateSessionAccount(request, updated);

            SessionUtil.recordActivity(request, "Transferred Rs. " + amount + " to " + toAccountNumber);

            String successMsg = String.format("Transfer of Rs. %.2f to Account %s was successful!", amount, toAccountNumber);
            response.sendRedirect(request.getContextPath() + "/user/dashboard?success=" + URLEncoder.encode(successMsg, "UTF-8"));

        } catch (InsufficientBalanceException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramToAccount", toAccountNumber);
            request.setAttribute("paramAmount", amountStr);
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/transfer.jsp").forward(request, response);
        } catch (AccountNotFoundException | InactiveAccountException | InvalidAmountException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("paramToAccount", toAccountNumber);
            request.setAttribute("paramAmount", amountStr);
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/transfer.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Transfer failed: " + e.getMessage());
            request.setAttribute("paramToAccount", toAccountNumber);
            request.setAttribute("paramAmount", amountStr);
            request.setAttribute("paramRemark", remark);
            request.getRequestDispatcher("/jsp/user/transfer.jsp").forward(request, response);
        }
    }
}

